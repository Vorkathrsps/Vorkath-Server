package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class DagannothSupreme extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(2855);
        new Projectile(entity, target, 475, 30, 45, 30, 25, 0,10,5).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 1, CombatType.RANGED).checkAccuracy().submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
