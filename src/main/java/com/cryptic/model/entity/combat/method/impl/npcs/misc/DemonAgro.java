package com.cryptic.model.entity.combat.method.impl.npcs.misc;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

public class DemonAgro implements AggressionCheck {
    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return true;
    }
}
