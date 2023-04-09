package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class DagannothSupreme extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(2855);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 475, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();
        hit.submit();
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
