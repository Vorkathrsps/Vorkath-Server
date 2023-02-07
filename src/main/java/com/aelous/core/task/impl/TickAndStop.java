package com.aelous.core.task.impl;

import com.aelous.core.task.Task;

public abstract class TickAndStop extends Task {

    /**
     * Creates a tickable with the specified amount of ticks.
     *
     * @param ticks The amount of ticks.
     */
    public TickAndStop(int ticks) {
        super("TickAndStop", ticks);
    }

    @Override
    public void execute() {
        stop();
        executeAndStop();
    }

    public abstract void executeAndStop();
}
