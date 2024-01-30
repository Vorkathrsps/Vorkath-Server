package com.cryptic.core.task;

import com.cryptic.core.task.impl.PlayerTask;
import com.google.common.collect.ImmutableList;
import com.cryptic.model.entity.Entity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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
        try {
            Task t;
            while ((t = pendingTasks.poll()) != null) {
                if (t.isRunning()) {
                    activeTasks.add(t);
                    taskNames.add("");
                }
            }

            Task[] tasks = activeTasks.stream().filter(t2 -> t2.getKey() == null || !(t2.getKey() instanceof Entity)).toArray(Task[]::new);
            for (Task task : tasks) {
                if (task.isRunning()) {
                    task.onTick();
                }
                if (!task.sequence()) {
                    activeTasks.remove(task);
                }
            }

        } catch (Exception e) {
            logger.error("task error", e);
        }

        //logger.info("it took "+end+"ms for processing tasks.");
    }

    public static void sequenceForMob(Entity entity) {
        if (!entity.isNpc()) {
            sequenceNormalMode(entity);
        } else {
            sequenceNormalMode(entity);
        }
    }

    private static final List<Task> sequenceTasks = new ObjectArrayList<>();

    private static void sequenceNormalMode(Entity entity) {
        List<Task> tasks = entity.activeTasks;
        sequenceTasks.addAll(tasks);
        for (Task task : sequenceTasks) {
            if (task.isRunning()) {
                task.onTick();
            }
            task.sequence();
        }
        sequenceTasks.clear();
        entity.activeTasks.removeIf(t -> !t.isRunning());
    }

    public static void submit(Task task) {
        if (!task.isRunning())
            return;
        task.onStart();
        if (task.isImmediate()) {
            task.execute();
        }

        if (task instanceof PlayerTask playerTask) {
            playerTask.getPlayer().setCurrentTask(task);
        }

        if (task.getKey() instanceof Entity) {
            ((Entity) task.getKey()).activeTasks.add(task);
        } else {
            pendingTasks.add(task);
        }
    }

    private static final List<Task> cancels = new ObjectArrayList<>();

    public static void cancelTasks(Object key) {
        try {
            if (key instanceof Entity entity) {
                cancels.addAll(entity.activeTasks);
                System.out.println(cancels.size());
                cancels.forEach(Task::stop);
                cancels.clear();
            } else {
                pendingTasks.stream().filter(t -> t != null && t.getKey() == key).forEach(Task::stop);
                activeTasks.stream().filter(t -> t != null && t.getKey() == key).forEach(Task::stop);
            }
        } catch (Exception e) {
            logger.error("cancel fail", e);
        }
    }

    public static String getActiveTaskNames() {
        StringBuilder taskNames = new StringBuilder();
        for (Task active : activeTasks) {
            if (active == null) continue;
            taskNames.append(active.getName()).append(", ");
        }
        if (taskNames.length() > 2) {
            taskNames = new StringBuilder(taskNames.substring(0, taskNames.length() - 2));
        }
        return taskNames.toString();
    }

    public static String getPendingTaskNames() {
        StringBuilder taskNames = new StringBuilder();
        for (Task pending : pendingTasks) {
            if (pending == null) continue;
            taskNames.append(pending.getName()).append(", ");
        }
        if (taskNames.length() > 2) {
            taskNames = new StringBuilder(taskNames.substring(0, taskNames.length() - 2));
        }
        return taskNames.toString();
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
