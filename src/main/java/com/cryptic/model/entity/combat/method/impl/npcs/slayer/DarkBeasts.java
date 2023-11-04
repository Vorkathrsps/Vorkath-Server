package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class DarkBeasts extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        if (entity.tile().distance(target.tile()) <= 2) {
            int tileDist = entity.tile().transform(1, 1).distance(target.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 130, 51, duration, 43, 31, 0, target.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }
}
