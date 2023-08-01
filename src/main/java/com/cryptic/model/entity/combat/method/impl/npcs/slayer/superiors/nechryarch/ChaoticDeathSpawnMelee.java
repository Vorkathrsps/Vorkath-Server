package com.cryptic.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Origin
 * april 02, 2020
 */
public class ChaoticDeathSpawnMelee extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
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
    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
