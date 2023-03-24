package com.aelous.core.task;

import com.aelous.GameServer;
import com.aelous.GameEngine;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.core.task.impl.TickableTask;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a periodic task that can be scheduled with a
 * {@link Task}.
 *
 * @author Graham
 */
public abstract class Task {

    private static final Logger logger = LogManager.getLogger(Task.class);

    /*
     * These tasks are set to run indefinitely.
     * We don't want MobFollowTask to run indefinitely except for pets,
     * but we handle that elsewhere.
     */
    private static final String[] IGNORED_TASKS = {"EarningPotential", "shanomi_shout_task", "TournamentCycleTask", "TournamentLobbyTask", "SaveTask", "HotspotTask", "MobFollowTask", "RegionalPetTask", "DidYouKnowTask", "WildernessActivityManagerTask", "WildernessBossEventTask", "BossEventCancelTask", "AddShopStockTask", "VenomTask", "AntifireTask", "WildernessKeyPluginTask", "TopPkersAnnouncementTask", "PoisonTask", "ImplingTask", "instancecheck"};

    /** The default key for every task. */
    public static final Object DEFAULT_KEY = new Object();

    /**
     * The name of the task. This can be used to identify long-running tasks.
     */
    private final String name;

    /**
     * The number of cycles between consecutive executions of this task.
     */
    private int delay;

    /**
     * A flag which indicates if this task should be executed once immediately.
     */
    private final boolean immediate;

    /**
     * The current 'count down' value. When this reaches zero the task will be
     * executed.
     */
    private int countdown;

    /**
     * A flag which indicates if this task is still running.
     */
    private boolean running = true;

    /**
     * Stores how long `this` task has been running for.
     */
    private int runDuration;

    public int getRunDuration() {
        return runDuration;
    }

    public void setEventRunning(boolean running) {
        this.running = running;
    }

    private Runnable onCompleted;

    /**
     * The task's owner
     */
    private Object key;

    public final Object getKey() {
        return key;
    }

    public String codeOrigin;

    /**
     * allow null keys (aka contextless / ownerless , runs w/o a player/npc bound to it)
     * @param key
     * @return
     */
    public final Task bind(Object key) {
        this.key = key;
        return this;
    }

    /**
     * Creates a new nameless task with a delay of 1 cycle.
     */
    public Task() {
        this("NamelessTask", 1);
    }

    /**
     * Creates a new task with a delay of 1 cycle.
     */
    public Task(String name) {
        this(name, 1);
    }

    /**
     * Creates a new task with a delay of 1 cycle and immediate flag.
     *
     * @param immediate A flag that indicates if for the first execution there
     *                  should be no delay.
     */
    public Task(String name, boolean immediate) {
        this(name, 1, immediate);
    }

    /**
     * Creates a new task with the specified delay.
     *
     * @param delay The number of cycles between consecutive executions of this
     *              task.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(String name, int delay) {
        this(name, delay, false);
        this.bind(DEFAULT_KEY);
    }

    /**
     * Creates a new task with the specified delay and immediate flag.
     *
     * @param delay     The number of cycles between consecutive executions of this
     *                  task.
     * @param immediate A flag which indicates if for the first execution there
     *                  should be no delay.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(String name, int delay, boolean immediate) {
        this(name, delay, DEFAULT_KEY, immediate);
    }

    /**
     * Creates a new task with the specified delay and immediate flag.
     *
     * @param delay     The number of cycles between consecutive executions of this
     *                  task.
     * @param immediate A flag which indicates if for the first execution there
     *                  should be no delay.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(String name, int delay, Object key, boolean immediate) {
        this.name = name;
        this.delay = delay;
        this.countdown = delay;
        this.immediate = immediate;
        this.bind(key);
        codeOrigin = Arrays.toString(StackWalker.getInstance().walk(s -> s.dropWhile(e -> Arrays.stream(new String[]{"task.java", "tickabletask.java"}).anyMatch(n -> e.getFileName().toLowerCase().equals(n)) || e.getMethodName().equalsIgnoreCase("<init>") || e.getMethodName().equalsIgnoreCase("repeatingtask")).limit(3).map(s1 -> s1.toString())
            .map(s2 -> {
                // kotlin.KtCommands$init$26.invoke(KtCommands.kt:293)
                // .setTimer(Poison.java:54)
                int dotfile = s2.lastIndexOf(".");
                int endfile = s2.substring(0, dotfile).lastIndexOf(".");
                int startfile = s2.substring(0, endfile).lastIndexOf(".");
                return s2.substring(startfile+1);
            }).collect(Collectors.toList())).toArray());
    }


    /**
     * Code run every gametick the task is active, NOT the same as {@linkplain Task#execute()} which only runs when
     * the {@linkplain Task#countdown} of a task is 0. Countdown is used to run a task every X game cycles.
     */
    protected void onTick() {

    }

    public void onStart() {

    }

    public void onStop() {
        if (onCompleted != null) {
            onCompleted.run();
        }
    }

    public Task onStop(Runnable r) {
        this.onCompleted = r;
        return this;
    }

    /** Determines if the task can be ran. */
    public boolean canRun() {
        return true;
    }

    /**
     * Checks if this task is an immediate task.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * Checks if the task is running.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if the task is stopped.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isStopped() {
        return !running;
    }

    /**
     * This method should be called by the scheduling class every cycle. It updates
     * the {@link #countdown} and calls the {@link #execute()} method if necessary.
     *
     * @return A flag indicating if the task is running.
     */
    public boolean sequence() {
        if (running) {
            long start = System.currentTimeMillis();
            long uptime = start - GameServer.startTime;
            increaseRunDuration();
            if (--countdown == 0) {
                execute();
                countdown = delay;
            }
            long elapsed = System.currentTimeMillis() - start;
            if (Arrays.stream(IGNORED_TASKS).noneMatch(task -> task.equals(getClass().getSimpleName())) && uptime > GameEngine.IGNORE_LAG_TIME && elapsed > 75) {
                logger.error(String.format("It took %s milliseconds to execute the %s task.", elapsed, (!getClass().getSimpleName().equals("") ? getClass().getSimpleName() : getClass().getName()) + " at " + codeOrigin));
            }
        }
        return running;
    }

    /**
     * Performs this task's action after the {@link Task#countdown} reaches zero, aka the delay between executions.
     * NOT the same as {@link Task#onTick()} which runs every game cycle regardless of remaining countdown.
     */
    protected abstract void execute();

    /**
     * Changes the delay of this task.
     *
     * @param delay The number of cycles between consecutive executions of this
     *              task.
     */
    public void setDelay(int delay) {
        if (delay > 0)
            this.delay = delay;
    }

    public int getDelay() {
        return this.delay;
    }

    /**
     * Get the name of the task
     *
     * @return task name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Stops this task. If you want to override this, use {@link #onStop()}. Tasks run for a maximum of 1 hour by default.
     * (see {@link Task#increaseRunDuration()})
     */
    public void stop() {
        //System.out.println("Stopping task initiated by: " + Utils.getStackTrace());
        //System.out.println("Stopping task " + getClassName() + " with " + getKeyName());
        running = false;
        onStop();
    }

    public String getClassName() {
        return (!getClass().getSimpleName().equals("") ? getClass().getSimpleName() : getClass().getName() + " (" + (!getName().equals("") ? getName() : "Unnamed") + ")");
    }

    public String getKeyName() {
        return (getKey() != null ? "key " + getKey() : "null key");
    }

    /**
     * Increases the run duration for this task and prints a warning message to
     * server console if duration exceeds threshold.
     */
    private void increaseRunDuration() {
        runDuration++;
        if (runDuration >= 6100 && Arrays.stream(IGNORED_TASKS).noneMatch(task -> task.equals(getName()))) {
            stop();
            logger.warn("Task " + getClassName() + " has been running for over an hour, and has been stopped! Source "+keyOrOrigin());
            Utils.sendDiscordInfoLog("Task " + getClassName() + " has been running for over an hour, and has been stopped! Source "+keyOrOrigin(), "warning");
        }
    }

    /**
     * a task that runs every 1 game tick. Aka repeatingTask
     */
    public static TickableTask repeatingTask(Consumer<TickableTask> r) {
        TickableTask task = new TickableTask(true, 1) {
            @Override
            protected void tick() {
                r.accept(this);
            }
        };
        TaskManager.submit(task);
        return task;
    }

    /**
     * a task that runs Once after {@code delay} ticks.
     */
    public static TickAndStop runOnceTask(int delay, Consumer<TickAndStop> r) {
        TickAndStop task = new TickAndStop(delay) {
            @Override
            public void executeAndStop() {
                r.accept(this);
            }
        };
        TaskManager.submit(task);
        return task;
    }

    public String keyOrOrigin() {
        return key != null && key instanceof String ? (String) key : codeOrigin == null ? "unknown src" : codeOrigin;
    }
}
