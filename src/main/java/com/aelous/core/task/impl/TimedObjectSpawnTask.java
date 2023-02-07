package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;

/**
 * A {@link Task} implementation which spawns a {@link GameObject}
 * and then despawns it after a period of time.
 * 
 * @author Professor Oak
 */
public class TimedObjectSpawnTask extends Task {

    /**
     * The temporary {@link GameObject}.
     *
     * This object will be deregistered once the task has finished executing.
     *
     */
    private final GameObject temp;

    /**
     * The amount of ticks this task has.
     */
    private final int ticks;

    /**
     * The action which should be executed
     * once this task has finished.
     */
    private final Runnable action;

    /**
     * The current tick counter.
     */
    private int tick = 0;

    /**
     * Constructs this task to spawn an object and then delete it
     * after a period of time.
     * @param temp
     * @param ticks
     */
    public TimedObjectSpawnTask(GameObject temp, int ticks, Runnable action) {
        super("TimedOBjectSpawnTask", 1, true);
        this.temp = temp;
        this.action = action;
        this.ticks = ticks;
    }

    /**
     * Executes this task.
     */
    @Override
    public void execute() {
        if (tick == 0) {
            ObjectManager.addObj(temp);
        } else if (tick >= ticks) {
            ObjectManager.removeObj(temp);

            action.run();

            stop();
        }
        tick++;
    }
}
