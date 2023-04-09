package com.aelous.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class WingmanSkree extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6955);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (45 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1199, 25, duration, 0, 0, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
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
