package com.cryptic.model.entity.combat.method.impl.npcs.godwars.armadyl;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;
import lombok.Getter;

public class KreeArraCombat extends CommonCombatMethod {
    @Getter
    private static final Area ENCAMPMENT = new Area(2823, 5295, 2843, 5309);

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) return false;

        int roll = Utils.random(2);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int durationRanged = (43 + 11 + (5 * tileDist));
        int durationMagic = (51 + -5 + (10 * tileDist));

        switch (roll) {
            case 0 -> {
                if (target.getCombat().getTarget() == this.entity) return false;
                if (withinDistance(1)) {
                    melee();
                }
            }
            case 1 -> {
                if (!withinDistance(8)) return false;
                ranged(durationRanged);
            }
            case 2 -> {
                if (!withinDistance(8)) return false;
                magic(durationMagic);
            }
        }
        return true;
    }

    public void melee() {
        entity.animate(6981);
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    public void ranged(int durationRanged) {
        if (!ProjectileRoute.hasLineOfSight(entity, target)) return;
        entity.animate(6980);
        Projectile p = new Projectile(entity, target, 1199, 43, durationRanged, 0, 0, 0, entity.getSize(), 5);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
    }

    public void magic(int durationMagic) {
        if (!ProjectileRoute.hasLineOfSight(entity, target)) return;
        entity.animate(6980);
        Projectile p = new Projectile(entity, target, 1200, 51, durationMagic, 0, 0, 0, entity.getSize(), 5);
        final int delay = (int) (p.getSpeed() / 30D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
     }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }
}
