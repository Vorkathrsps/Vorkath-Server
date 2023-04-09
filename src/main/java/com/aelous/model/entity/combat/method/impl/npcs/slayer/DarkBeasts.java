package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class DarkBeasts extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
       if (entity.tile().distance(target.tile()) <= 2) {
           int tileDist = entity.tile().transform(1, 1).distance(target.tile());
           new Projectile(entity, target, 130, 50, 12 * tileDist, 40, 30, 0).sendProjectile();
           target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
       }
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
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
}
