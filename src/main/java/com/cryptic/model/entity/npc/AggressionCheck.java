package com.cryptic.model.entity.npc;

import com.cryptic.model.entity.Entity;

public interface AggressionCheck {

    boolean shouldAgro(Entity entity, Entity victim);

}
