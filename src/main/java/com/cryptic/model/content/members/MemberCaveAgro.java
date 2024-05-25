package com.cryptic.model.content.members;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

public class MemberCaveAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return !entity.tile().memberCave();
    }

}
