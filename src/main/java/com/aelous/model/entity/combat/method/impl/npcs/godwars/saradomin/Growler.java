package com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class Growler extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7037);
        entity.graphic(1182);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (25 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1183, 25, duration, 0, 0, 0, target.getSize(), 10);
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
