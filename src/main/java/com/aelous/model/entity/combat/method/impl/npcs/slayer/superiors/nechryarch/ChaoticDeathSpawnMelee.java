package com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 02, 2020
 */
public class ChaoticDeathSpawnMelee extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
