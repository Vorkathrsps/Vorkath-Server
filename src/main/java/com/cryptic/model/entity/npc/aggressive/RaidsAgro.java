package com.cryptic.model.entity.npc.aggressive;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

public class RaidsAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return true;
    }
}
