package com.aelous.model.entity.npc.aggressive;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.AggressionCheck;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 19, 2021
 */
public class MenInBlackAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return false;
    }
}
