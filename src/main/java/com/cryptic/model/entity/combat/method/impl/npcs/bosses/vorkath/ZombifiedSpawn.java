package com.cryptic.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class ZombifiedSpawn extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
