package com.aelous.model.entity.combat.method.impl.npcs.misc;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class Spinolyp extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Projectile(entity, target, 94, 0, 20, 43, 31, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
