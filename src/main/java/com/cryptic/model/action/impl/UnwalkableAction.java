package com.cryptic.model.action.impl;

import com.cryptic.model.action.Action;
import com.cryptic.model.action.policy.WalkablePolicy;
import com.cryptic.model.entity.Entity;

/**
 * @author PVE
 * @Since september 04, 2020
 */
public abstract class UnwalkableAction extends Action<Entity> {

    public UnwalkableAction(Entity entity, int delay, boolean instant) {
        super(entity, delay, instant);
    }

    public UnwalkableAction(Entity entity, int delay) {
        super(entity, delay);
    }

    @Override
    public boolean prioritized() {
        return super.prioritized();
    }

    @Override
    public WalkablePolicy getWalkablePolicy() {
        return WalkablePolicy.NON_WALKABLE;
    }

    @Override
    public String getName() {
        return "";
    }

}
