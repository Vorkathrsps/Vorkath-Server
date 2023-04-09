package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.minions;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 16, 2022
 */
public class BloodReaver extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 2000, 35, duration, 31, 0, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC);
        hit.checkAccuracy().submit();
        if(hit.isAccurate()) {
            target.graphic(2001, GraphicHeight.HIGH, p.getSpeed());
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
