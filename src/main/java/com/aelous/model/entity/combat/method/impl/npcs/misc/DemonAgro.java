package com.aelous.model.entity.combat.method.impl.npcs.misc;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

public class DemonAgro implements AggressionCheck {
    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return true;
    }
}
