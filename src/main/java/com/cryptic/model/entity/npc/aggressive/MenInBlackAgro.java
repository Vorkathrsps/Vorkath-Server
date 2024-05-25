package com.cryptic.model.entity.npc.aggressive;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

/**
 * @Author Origin
 * @Since October 19, 2021
 */
public class MenInBlackAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return false;
    }
}
