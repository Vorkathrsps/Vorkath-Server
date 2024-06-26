package com.cryptic.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class GrimspikeCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6154);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1220, 41, duration, 43, 31, 0, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
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
