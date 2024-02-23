package com.cryptic.model.entity.combat.method.impl.npcs.godwars.zamorak;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class BalfrugCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(4630);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (25 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1227, 25, duration, 1, 5, 0, entity.getSize(), 10);
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
