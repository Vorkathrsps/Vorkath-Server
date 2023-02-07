package com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class WingmanSkree extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(6955);
        new Projectile(entity, target, 1201, 25, 65, 80, 25, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
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
