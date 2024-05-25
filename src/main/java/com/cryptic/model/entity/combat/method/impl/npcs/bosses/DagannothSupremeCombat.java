package com.cryptic.model.entity.combat.method.impl.npcs.bosses;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;

public class DagannothSupremeCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }
        entity.animate(2855);
        int tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p = new Projectile(tile, target, 475, 41, duration, 45, 31, 16, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);
        hit.submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
