package com.aelous.model.content.members;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

public class MemberCaveAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return !entity.tile().memberCave();
    }
}
