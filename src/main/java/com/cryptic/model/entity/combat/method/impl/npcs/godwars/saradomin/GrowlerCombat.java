package com.cryptic.model.entity.combat.method.impl.npcs.godwars.saradomin;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class GrowlerCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7037);
        entity.graphic(1182);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (25 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1183, 25, duration, 0, 0, 0, entity.getSize(), 10);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
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
