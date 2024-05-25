package com.cryptic.model.entity.combat.method.impl.npcs.misc;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class FireGiantCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return false;
        entity.animate(entity.attackAnimation());
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
