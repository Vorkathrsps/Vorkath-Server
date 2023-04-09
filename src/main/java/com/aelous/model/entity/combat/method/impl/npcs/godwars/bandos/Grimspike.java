package com.aelous.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class Grimspike extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6154);
        new Projectile(entity, target, 1220, 30, 65, 25, 25, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
