package com.cryptic.core.task;

import com.cryptic.core.task.impl.PlayerTask;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.cryptic.GameServer;
import com.cryptic.GameEngine;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.NpcPerformance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class TaskManager {

    private static final Logger logger = LogManager.getLogger(TaskManager.class);

    private final static Queue<Task> pendingTasks = new LinkedList<>();

    private final static List<Task> activeTasks = new LinkedList<>();

    private final static ArrayList<String> taskNames = new ArrayList<>();

    private TaskManager() {
        throw new UnsupportedOperationException(
            "This class cannot be instantiated!");
    }

    public static void sequence() {
        long start = System.currentTimeMillis();
        try {
            Task t;
            while ((t = pendingTasks.poll()) != null) {
                if (t.isRunning()) {
                    activeTasks.add(t);
                    taskNames.add("");
                }
            }

            // Shadowrs: entity tasks are uniquely executed after packets on each player sequence.
            Task[] tasks = activeTasks.stream().filter(t2 -> t2.getKey() == null || !(t2.getKey() instanceof Entity)).toArray(Task[]::new);
            for (Task task : tasks) {
                Stopwatch stopwatch1 = Stopwatch.createStarted();
                if (task.isRunning()) {
                    task.onTick();
                }
                if (!task.sequence()) {
                    activeTasks.remove(task);
                }
                stopwatch1.stop();
                long ns = stopwatch1.elapsed().toMillis();
                if (ns > 1 && GameServer.properties().displayCycleLag) { // 1ms
                    logger.error("task took {}ms from {}", ns, task.keyOrOrigin());
                }
            }

        } catch(Exception e) {
            logger.error("task error", e);
        }

        //logger.info("it took "+end+"ms for processing tasks.");
    }

    public static void sequenceForMob(Entity entity) {
        if (!entity.isNpc()) {
            sequenceNormalMode(entity);
        } else {
            if (NpcPerformance.PERF_CHECK_MODE_ENABLED) {
                NPC.accumulateRuntimeTo(() -> {
                    sequenceForNpcPerformanceMode(entity);
                }, d -> NpcPerformance.F += d.toNanos());
            } else {
                // yes for npcs
                sequenceNormalMode(entity);
            }
        }
        // Shadowrs: entity tasks are uniquely executed after packets on each player sequence.
    }

    private static final List<Task> sequenceTasks = new ObjectArrayList<>();

    private static void sequenceNormalMode(Entity entity) {
        List<Task> tasks = entity.activeTasks;
        long lol = 0L;
        sequenceTasks.addAll(tasks); //whts difference between using local temp var to field ?
        // before it does new ArrayList(tasks) which creates new arraylist each time and new entry for all
        // which allocates hella memory, oh shit LOL
        for (Task task : sequenceTasks) { //fix plox
            if (task.isRunning()) {
                task.onTick();
            }
            task.sequence();
        }
        sequenceTasks.clear();
        entity.activeTasks.removeIf(t -> !t.isRunning());
    }

    private static void sequenceForNpcPerformanceMode(Entity entity) {
        List<Task> tasks = entity.activeTasks;
        long lol = 0L;
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (Task task : new ArrayList<>(tasks)) {
            Stopwatch stopwatch1 = Stopwatch.createStarted();
            if (task.isRunning()) {
                task.onTick();
            }
            task.sequence();
            stopwatch1.stop();
            lol += stopwatch1.elapsed(TimeUnit.NANOSECONDS);
            if (NpcPerformance.DETAL_LOG_ENABLED && entity.isNpc()) {
                long ns = stopwatch1.elapsed().toNanos();
                if (ns > 500_000) { // 0.1ms
                    String src = task.keyOrOrigin();
                    NpcPerformance.TaskPerfEntry entry = new NpcPerformance.TaskPerfEntry();
                    entry.task = task;
                    entry.duration = stopwatch1.elapsed();
                    entry.name = src;
                    entity.getAsNpc().performance.tmLog.addOffender(entry);
                }
                entity.getAsNpc().performance.tmLog.tasks++;
            }
        }
        entity.getAsNpc().performance.tmLog.sumRuntimeTasks = Duration.ofNanos(lol);
        stopwatch.stop();
        if (stopwatch.elapsed().toMillis() > 500) { // 0.5s
            logger.error("npc had {} tasks taking {} ms for mob {}. sum rt: {} ns by {} tasks, task dump: {}",
                tasks.size(), stopwatch.elapsed().toMillis(), entity, entity.getAsNpc().performance.tmLog.sumRuntimeTasks.toNanos(),
                entity.getAsNpc().performance.tmLog.tasks, entity.getAsNpc().performance.tmLog.build());
        }
        entity.activeTasks.removeIf(t -> !t.isRunning());
    }

    public static void submit(Task task) {
        if (!task.isRunning())
            return;
        task.onStart();
        if (task.isImmediate()) {
            task.execute();
        }

        if (task instanceof PlayerTask) {
            PlayerTask playerTask = (PlayerTask) task;
            playerTask.getPlayer().setCurrentTask(task);
        }

        if (task.getKey() instanceof Entity) {
            ((Entity)task.getKey()).activeTasks.add(task);
        } else {
            pendingTasks.add(task);
        }
    }

    public static void cancelTasks(Object key) {
        try {
            if (key instanceof Entity) {
                Entity entity = (Entity) key;
                // avoid concurrentmod ex. cause is unknown
                new ArrayList<>(entity.activeTasks).forEach(Task::stop);
            } else {
                pendingTasks.stream().filter(t -> t != null && t.getKey() == key).forEach(Task::stop);
                activeTasks.stream().filter(t -> t != null && t.getKey() == key).forEach(Task::stop);
            }
        } catch(Exception e) {
            logger.error("cancel fail", e);
        }
    }

    public static String getActiveTaskNames() {
        String taskNames = "";
        for (Task active : activeTasks) {
            if (active == null) continue;
            taskNames += active.getName() + ", ";
        }
        if (taskNames.length() > 2) {
            taskNames = taskNames.substring(0, taskNames.length() - 2);
        }
        return taskNames;
    }

    public static String getPendingTaskNames() {
        String taskNames = "";
        for (Task pending : pendingTasks) {
            if (pending == null) continue;
            taskNames += pending.getName() + ", ";
        }
        if (taskNames.length() > 2) {
            taskNames = taskNames.substring(0, taskNames.length() - 2);
        }
        return taskNames;
    }
    public static int getTaskAmountByName(String taskName) {
        int taskCount = 0;
        for (Task active : activeTasks) {
            if (active == null) continue;
            if (active.getName().equalsIgnoreCase(taskName)) {
                taskCount++;
            }
        }
        for (Task active : pendingTasks) {
            if (active == null) continue;
            if (active.getName().equalsIgnoreCase(taskName)) {
                taskCount++;
            }
        }
        return taskCount;
    }
    public static int getTaskAmount() {
        return (pendingTasks.size() + activeTasks.size());
    }

    public static ImmutableList<Task> getActiveTasks() {
        return ImmutableList.copyOf(activeTasks);
    }
}
