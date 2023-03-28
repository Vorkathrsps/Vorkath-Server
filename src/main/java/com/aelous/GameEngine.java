package com.aelous;

import com.aelous.core.CountingThreadFactory;
import com.aelous.core.SingleThreadFactory;
import com.aelous.core.TimesCycle;
import com.aelous.model.inter.clan.ClanRepository;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.utility.NpcPerformance;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * A model that handles game thread initialization and processing. All game related code is ran on
 * the game thread. Warning: Be careful about putting code like services in here, putting the
 * DatabaseService in here silently crashed the game with logging in with no exceptions being thrown
 * whatsoever. DatabaseService is now in Server.
 *
 * @author lare96
 */
public final class GameEngine implements Runnable {

    public static boolean successfulCycle = false;
    public static boolean successfulTasks = false;
    public static boolean successfulWorld = false;
    public static boolean successfulGroundItem = false;
    public static volatile boolean shutdown;
    private static final int GAME_TICK_DURATION = 600;
    public static final int IGNORE_LAG_TIME =
            GameServer.properties().ignoreGameLagDetectionMilliseconds;
    private static final Logger logger = LogManager.getLogger(GameEngine.class);
    private static final Marker markPerf = MarkerManager.getMarker("perf");

    /** ticks between printing debug info */
    private static int infoTickCountdown = 0;

    public static int gameTicksIncrementor;

    public static long totalCycleTime;
    public static int totalPendingThresholdWarningMs = 250;
    public static int totalGroundThresholdWarningMs = 250;
    public static int totalTotalThresholdWarningMs = 250;
    public static boolean autoEnableTimerDebug = true;
    public static long lastAutoEnableDebugTriggered = System.currentTimeMillis() - (1000 * 60 * 60);

    /** A queue of synchronization tasks. */
    private final Queue<Runnable> syncTasks = new ConcurrentLinkedQueue<>();

    /** The game thread. */
    private final ScheduledExecutorService gameThread =
            Executors.newSingleThreadScheduledExecutor(
                    new ThreadFactoryBuilder()
                            .setNameFormat(GameConstants.SERVER_NAME + " GameThread")
                            .build());

    /**
     * A thread pool that will handle low-priority asynchronous tasks. This thread pool has threads
     * known as "worker threads".
     */
    private final ListeningExecutorService lowPriorityThreadPool;

    /** A thread pool that will handle discord HTTP requests. */
    private final ListeningExecutorService discordThreadPool;

    /** Creates this game engine. */
    private GameEngine() {
        int nWorkers =
                Math.max(
                        Runtime.getRuntime().availableProcessors() / 2,
                        2); // Workers should be cores / 2 not * 2 since we don't want to peg the
        // CPU and lag in-game.
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        nWorkers, nWorkers, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(false);
        executor.setThreadFactory(
                new CountingThreadFactory("" + GameConstants.SERVER_NAME + "WorkerThread"));
        lowPriorityThreadPool = MoreExecutors.listeningDecorator(executor);

        ThreadPoolExecutor discordExecutor =
                new ThreadPoolExecutor(
                        1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        discordExecutor.allowCoreThreadTimeOut(false);
        discordExecutor.setThreadFactory(
                new SingleThreadFactory("" + GameConstants.SERVER_NAME + "DiscordThread"));
        discordThreadPool = MoreExecutors.listeningDecorator(discordExecutor);
    }

    /** Initializes this {@link GameEngine}. */
    public void start() {
        // Start game engine..
        gameThread.scheduleAtFixedRate(this, 0, GAME_TICK_DURATION, TimeUnit.MILLISECONDS);
        World.getWorld().ls.start();
    }

    /**
     * Queues a task from another thread to be ran on the game thread. Use this when you want to
     * execute game-related code, but you're not on the game thread.
     */
    public void addSyncTask(Runnable runnable) {
        syncTasks.add(runnable);
    }

    /**
     * Submits a low-priority task to be ran asynchronously (meaning off of the game thread). You
     * want to use this for things like saving files, connecting to databases, etc. in order to keep
     * the game thread running as fast as possible (slower game thread = more lag!).
     *
     * @return A listenable future that essentially lets you track the completion of the task (and
     *     add a completion listener, obviously).
     */
    public ListenableFuture<?> submitLowPriority(Runnable runnable) {
        return lowPriorityThreadPool.submit(runnable);
    }

    public ListenableFuture<?> submitDiscord(Runnable runnable) {
        return discordThreadPool.submit(runnable);
    }

    /**
     * Submits a low-priority task to be ran asynchronously (meaning off of the game thread). You
     * want to use this for things like saving files, connecting to databases, etc. in order to keep
     * the game thread running as fast as possible (slower game thread = more lag!).
     *
     * @return A listenable future that essentially lets you track the completion of the task (and
     *     add a completion listener, obviously).
     */
    public <V> ListenableFuture<V> submitLowPriority(Callable<V> callable) {
        return lowPriorityThreadPool.submit(callable);
    }

    public <V> ListenableFuture<V> submitDiscord(Callable<V> callable) {
        return discordThreadPool.submit(callable);
    }

    /** Gracefully shuts down the server. */
    public void shutdown() {
        if (shutdown) {
            System.err.println("Shutdown already called");
            return;
        }
        shutdown = true;
        logger.info("Starting graceful shutdown...");

        logger.info("Stopping login service....");
        World.getWorld().ls.stop();

        // Run all pending tasks.
        logger.info("Running all pending tasks...");
        runPendingTasks();

        // Save miscellaneous important things (databases, clans, tradingpost etc.)
        logger.info("Saving misc. services...");
        ClanRepository.save();

        try {
            TradingPost.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // First, logout all players.
        logger.info("Waiting for all players to logout...");
        World.getWorld().getPlayers().forEach(Player::requestLogout);

        TaskManager.submit(
                new Task("GameEngineGracefulShutdownTask", 1, true) {
                    @Override
                    protected void execute() {

                        // Keep checking until all players have disconnected, and the logout service
                        // completes.
                        if (World.getWorld().getPlayers().size() == 0
                                && World.getWorld().ls.ONLINE.isEmpty()) {

                            logger.info(
                                    "All players removed and saved. The shutdown hook is now"
                                            + " shutting down remaining services...");
                            // Waits for logout service to finish queued actions, but beware logout
                            // code isn't run soley on the executor! isEmpty must be enforced too.

                            // World.getWorld().getLogoutService().stopAsync().awaitTerminated();

                            logger.info("Server down complete.");
                            System.exit(0);
                        }
                    }
                });
    }

    public static TimesCycle profile;
    public Deque<Long> recentTicks = Queues.newArrayDeque();

    @Override
    public void run() {
        try {
            successfulCycle = false;
            successfulTasks = false;
            successfulWorld = false;
            successfulGroundItem = false;

            profile = new TimesCycle();
            long start = System.currentTimeMillis();

            // The uptime equals the server start time minus the cycle start time, displayed in ms.
            long uptime = start - GameServer.startTime;

            // Process everything related to the game!
            long startPending = System.currentTimeMillis();
            runPendingTasks();
            long totalPending = System.currentTimeMillis() - startPending;
            successfulTasks = true;

            World.getWorld().sequence();
            successfulWorld = true;

            long startGround = System.currentTimeMillis();
            GroundItemHandler.pulse();
            long totalGround = System.currentTimeMillis() - startGround;
            successfulGroundItem = true;

            var pastTime = (System.currentTimeMillis() - start);
            String osName = System.getProperty("os.name");
            String osNameMatch = osName.toLowerCase();
            profile.total = pastTime;
            recentTicks.add(pastTime);
            if (recentTicks.size() > 10)
                recentTicks.removeFirst();
            //logger.info("average-cycle-last-10-ticks: " + (recentTicks.stream().mapToLong(l ->l).sum() / 10L) + "ms");

            if (profile.total > 50)
                profile.computeAnd(c -> logger.trace(markPerf, c.COMPUTED_MSG));
            lagChecks(uptime, totalPending, totalGround, osNameMatch);

            World.getWorld().benchmark.reset();
            successfulCycle = true;
            gameTicksIncrementor++;
            totalCycleTime += profile.total;
        } catch (Throwable t) {
            logger.error("help", t);
            World.getWorld().getPlayers().forEach(Player::synchronousSave);
        } finally {
            if (!successfulCycle) {
                logger.fatal("Game Engine Cycle was not successful.");
            }
            if (!successfulTasks) {
                logger.fatal("Game Engine Sync Tasks were not successful.");
            }
            if (!successfulWorld) {
                logger.fatal("Game Engine World was not successful.");
                for (int counter = 0; counter < World.getWorld().section.length; counter++) {
                    if (!World.getWorld().section[counter]) {
                        logger.fatal(
                                "Game Engine World section " + counter + " was not successful.");
                        // Player sequencing went wrong, let's find out what happened.
                        if (counter == 5) {
                            for (int counter2 = 1;
                                    counter2 < World.getWorld().getPlayers().size() + 1;
                                    counter2++) {
                                Player player = World.getWorld().getPlayers().get(counter2);
                                if (player != null) {
                                    // logger.info("Player was not null when logging");
                                    for (int counter3 = 0;
                                            counter3 < player.section.length;
                                            counter3++) {
                                        if (!player.section[counter3]) {
                                            logger.fatal(
                                                    "Player "
                                                            + player.getUsername()
                                                            + " section "
                                                            + counter3
                                                            + " was not successful.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!successfulGroundItem) {
                logger.fatal("Game Engine Ground Item was not successful.");
            }
        }
    }

    private void lagChecks(long uptime, long totalPending, long totalGround, String osNameMatch) {
        boolean printTime =
                GameServer.properties().displayCycleLag
                        && uptime > IGNORE_LAG_TIME
                        && (!GameServer.properties().linuxOnlyDisplayCycleLag
                                || osNameMatch.contains("linux"));
        if (printTime && totalPending > totalPendingThresholdWarningMs) {
            logger.trace(
                    "Pending Tasks cycle time greater than {} ms. Pending Tasks cycle time was: {}"
                            + " ms.",
                    totalPendingThresholdWarningMs,
                    profile.total);
        }
        if (printTime && totalGround > totalGroundThresholdWarningMs) {
            logger.trace(
                    "GroundItem cycle time greater than {} ms. GroundItem cycle time was: {} ms.",
                    totalGroundThresholdWarningMs,
                    profile.total);
        }
        // We only want to calculate the cycle lag if the uptime is greater than X number of
        // seconds.
        // In other words, only calculate the cycle lag after the GameServer is done loading.
        if (printTime && profile.total > totalTotalThresholdWarningMs) {
            profile.computeAnd(c -> logger.trace(c.COMPUTED_MSG));
        }
        if (!GameServer.properties().useInformationCycle
                || infoTickCountdown++ == GameServer.properties().informationCycleCount) {
            infoTickCountdown = 0;
            profile.computeAnd(
                    c -> {
                        logger.info(c.COMPUTED_MSG);
                    });
        }
        // cycle was over 400 but npcperf was off, turn it on to log complex info next cycle
        // hopefully next cycle is just as bad as this one otherwise this is useless
        if (autoEnableTimerDebug && profile.total > 400 && !NpcPerformance.DETAL_LOG_ENABLED) {
            NpcPerformance.DETAL_LOG_ENABLED = true;
            NpcPerformance.PERF_CHECK_MODE_ENABLED = true;
            lastAutoEnableDebugTriggered = System.currentTimeMillis();
        }
        if (profile.total > 600) {
            // a cycle 600 will result in visual lag (not fps lag) ingame - unresponsive
            // movement/actions.
            profile.computeAnd(c -> logger.trace(profile.COMPUTED_MSG));
        }
    }

    private void runPendingTasks() {
        while (!syncTasks.isEmpty()) {
            Runnable pending = syncTasks.poll();
            if (pending == null) {
                break;
            }
            try {
                pending.run();
            } catch (Exception e) {
                logger.error("help", e);
            }
        }
    }

    /**
     * The game engine, executed by {@link ScheduledExecutorService}. The game engine's cycle rate
     * is normally 600 ms.
     */
    private static final GameEngine instance = new GameEngine();

    public static GameEngine getInstance() {
        if (instance == null) {
            logger.fatal("Could not get GameEngine singleton!");
            System.exit(0);
        }
        return instance;
    }
}
