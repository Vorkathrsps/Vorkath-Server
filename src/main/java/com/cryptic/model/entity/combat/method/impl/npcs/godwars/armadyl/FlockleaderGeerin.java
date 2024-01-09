package com.cryptic.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class FlockleaderGeerin extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6956);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (43 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1192, 29, duration, 95, 33, 0, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }
}
