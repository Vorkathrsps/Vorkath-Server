package com.aelous.model.entity.npc;

import com.aelous.model.entity.Entity;

public interface AggressionCheck {

    boolean shouldAgro(Entity entity, Entity victim);

}
