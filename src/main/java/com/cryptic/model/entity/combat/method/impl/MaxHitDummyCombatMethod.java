package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.model.entity.Entity;

/**
 * @author Origin | February, 14, 2021, 12:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MaxHitDummyCombatMethod extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        return false;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
