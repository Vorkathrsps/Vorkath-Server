package com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 02, 2020
 */
public class ChaoticDeathSpawnMagic extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 393, 40, 55, 31, 43, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 7;
    }
    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
