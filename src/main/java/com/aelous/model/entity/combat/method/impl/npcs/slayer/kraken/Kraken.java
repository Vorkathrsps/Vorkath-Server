package com.aelous.model.entity.combat.method.impl.npcs.slayer.kraken;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class Kraken extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 156, 51, duration, 43, 31, 16, 2, 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if (hit.isAccurate()) {
            target.graphic(157, GraphicHeight.MIDDLE, p.getSpeed());
        } else {
            target.graphic(85, GraphicHeight.LOW, p.getSpeed());
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
