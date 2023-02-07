package com.aelous.core.task.impl;

import com.aelous.core.task.Task;

/**
 * Similar to {@link Task} but exposes the {@linkplain TickableTask#tick} field so the user can run logic depending on a state/stage
 * the task is at.
 * <br> WARNING this is NOT  chain and isnt auto-interrupted by {@code stopActions}
 */
public abstract class TickableTask extends Task {

    public int tick;

    public TickableTask(boolean instant, int delay) {
        super("TickableTask", delay, instant);
    }

    public TickableTask() {
        super("TickableTask", 1, false);
    }

    protected abstract void tick();

    @Override
    public void execute() {
        tick();
        tick++;
    }
}
