package com.aelous.model.action;

import com.google.common.base.Stopwatch;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.Entity;
import com.aelous.utility.NpcPerformance;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

/**
 * The class which manages {@link Action}s executed by mobs.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class ActionManager {

    /**  A queue of {@link Action} object. */
    private final Queue<Action<?>> queuedActions = new ArrayDeque<>();

    /** The current action.  */
    private Action<?> currentAction = null;

    /** Sequences the pending actions, and as soon as the current action  has stopped, it executes the head of the queue.  */
    public void sequence() {
        if (queuedActions.isEmpty() || (currentAction != null && currentAction.isRunning())) {
            return;
        }

        execute(this.currentAction = queuedActions.poll(), true);
    }

    /** Queues the specified {@code action}.  */
    public <A extends Action<?>> void queue(A action) {
        queuedActions.add(action);
    }

    public <A extends Action<?>> void execute(A action) {
        execute(action, true);
    }

    /**  Adds an <code>Action</code> to the queue.  */
    public <A extends Action<?>> void execute(A action, boolean override) {
        //if current action is priorized stop.
        if (currentAction != null && currentAction.isRunning() && currentAction.prioritized()) {
            return;
        }

        //if there is already an action active and we don't want to override
        //we ignore.
        //if u don't want to override but you also dont want to ignore
        //queue the action.
        if (currentAction != null && currentAction.isRunning() && !override) {
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        //if there is an action active, make sure we stop it before running
        //another action.
        if (currentAction != null && currentAction.isRunning()) {
            currentAction.stop();
        }

        //finally submit the action.
        TaskManager.submit(currentAction = action);
        stopwatch.stop();
        if (NpcPerformance.DETAL_LOG_ENABLED && stopwatch.elapsed().toNanos() > 100_000) {
            System.err.println("sketchy "+stopwatch.elapsed().toNanos()+" ns by:" + currentAction.getMob().getMobName() + "Loc: " + currentAction.keyOrOrigin());
        }
    }

    /** Cancels all the queued actions by stopping them and removing all elements from the queue.  */
    private void cancelQueuedActions() {
        queuedActions.forEach(Action::stop);
        queuedActions.clear();
    }

    /** Cancels the current {@link Action} for the underlying {@link Mob}. */
    public void cancel() {
        if (currentAction != null && currentAction.isRunning() && currentAction.prioritized()) {
            currentAction.stop();
            currentAction = null;
        }
    }

    /**  Resets all the actions for the underlying {@link Mob}. */
    public void reset() {
        cancel();
        cancelQueuedActions();
    }

    /** Purges actions in the queue with a <code>WalkablePolicy</code> of <code>NON_WALKABLE</code>.  */
    public void clearNonWalkableActions() {
        if (currentAction != null && currentAction.getWalkablePolicy() == WalkablePolicy.NON_WALKABLE) {
            currentAction.stop();
            currentAction = null;
        }

        for (Action<?> actionEvent : queuedActions) {
            if (actionEvent.getWalkablePolicy() != WalkablePolicy.WALKABLE) {
                actionEvent.stop();
                queuedActions.remove(actionEvent);
            }
        }
    }

    /** Purges actions in the queue with a <code>WalkablePolicy</code> of <code>NON_WALKABLE</code>. */
    public void cancel(String name) {
        if (currentAction != null && Objects.equals(currentAction.getName(), name)) {
            currentAction.stop();
            currentAction = null;
        }

        for (Action<?> actionEvent : queuedActions) {
            if (Objects.equals(actionEvent.getName(), name)) {
                actionEvent.stop();
                queuedActions.remove(actionEvent);
            }
        }
    }

    /** Gets the current action. */
    public Action<?> getCurrentAction() {
        return currentAction;
    }

    @Override
    public String toString() {
        return String.format("ActionManager[size=%s, action=%s]", queuedActions.size(), currentAction);
    }


}
