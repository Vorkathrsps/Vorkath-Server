package com.aelous.model.entity.npc.aggressive;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

public class RaidsAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return true;
    }
}
