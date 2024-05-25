package com.cryptic.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

/**
 * @author Origin
 * april 02, 2020
 */
public class ChaoticDeathSpawnRangeCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 393, 40, 55, 31, 43, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
