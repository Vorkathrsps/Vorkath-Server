package com.cryptic;

import com.cryptic.core.CountingThreadFactory;
import com.cryptic.core.SingleThreadFactory;
import com.cryptic.core.TimesCycle;
import com.cryptic.model.inter.clan.ClanRepository;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;

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
    public static AtomicBoolean shutdown = new AtomicBoolean(false);
    private static final long GAME_TICK_DURATION = 600L;
    public static final int IGNORE_LAG_TIME =
        GameServer.properties().ignoreGameLagDetectionMilliseconds;
    private static final Logger logger = LogManager.getLogger(GameEngine.class);
    private static final Marker markPerf = MarkerManager.getMarker("perf");

    /**
     * ticks between printing debug info
     */
    private static int infoTickCountdown = 0;

    public static int gameTicksIncrementor;

    public static long totalCycleTime;

    /**
     * A queue of synchronization tasks.
     */
    private final Queue<Runnable> syncTasks = new ConcurrentLinkedQueue<>();

    /**
     * The game thread.
     */
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

    /**
     * A thread pool that will handle discord HTTP requests.
     */
    private final ListeningExecutorService discordThreadPool;

    /**
     * Creates this game engine.
     */
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

    /**
     * Initializes this {@link GameEngine}.
     */
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
     * add a completion listener, obviously).
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
     * add a completion listener, obviously).
     */
    public <V> ListenableFuture<V> submitLowPriority(Callable<V> callable) {
        return lowPriorityThreadPool.submit(callable);
    }

    public <V> ListenableFuture<V> submitDiscord(Callable<V> callable) {
        return discordThreadPool.submit(callable);
    }

    /**
     * Gracefully shuts down the server.
     */
    public void shutdown() {
        if (shutdown.get()) {
            System.err.println("Shutdown already called");
            return;
        }

        shutdown.getAndSet(true);

        logger.info("Starting graceful shutdown...");

        try {
            logger.info("Stopping login service....");
            World.getWorld().ls.stop();
        } finally {
            try {
                logger.info("Stopping Pending Tasks...");
                runPendingTasks();
            } finally {
                try {
                    BooleanSupplier emptyWorld = () -> World.getWorld().getPlayers().size() == 0 && World.getWorld().ls.ONLINE.isEmpty();
                    BooleanSupplier repositoriesSaved = () -> TradingPost.saved.get() && ClanRepository.saved.get();
                    BooleanSupplier readyForShutdown = () -> emptyWorld.getAsBoolean() && repositoriesSaved.getAsBoolean();
                    Chain.noCtx().name("GameEngineShutDownTask").runFn(1, () -> {
                        logger.info("Waiting for all players to logout...");
                        World.getWorld().getPlayers().forEach(Player::requestLogout);
                    }).waitUntil(1, emptyWorld, () -> {
                        logger.info("All players removed and saved. The shutdown hook is now" + " shutting down remaining services...");
                        ClanRepository.save();
                        TradingPost.save();
                        logger.info("Saving Repository Services...");
                    }).waitUntil(1, readyForShutdown, () -> {
                        gameThread.shutdown();
                        logger.info("Game Thread ShutDown Complete.");
                        System.exit(0);
                    });
                } catch (Exception e) {
                    logger.info((Marker) Level.SEVERE, "Error in shutdown task.");
                    e.printStackTrace();
                }
            }
        }
    }

    public Deque<Long> recentTicks = Queues.newArrayDeque();

    @Override
    public void run() {
        try {
            successfulCycle = false;
            successfulTasks = false;
            successfulWorld = false;
            successfulGroundItem = false;

            long start = System.currentTimeMillis();

            runPendingTasks();
            successfulTasks = true;

            World.getWorld().sequence();
            successfulWorld = true;

            GroundItemHandler.pulse();
            successfulGroundItem = true;

            var pastTime = (System.currentTimeMillis() - start);

            recentTicks.add(pastTime);
            if (recentTicks.size() > 10)
                recentTicks.removeFirst();

            successfulCycle = true;
            gameTicksIncrementor++;

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
                                    logger.info("Player was not null when logging");
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
