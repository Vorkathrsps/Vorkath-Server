package com.cryptic.model.entity.combat.method.impl.npcs.misc;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.map.route.routes.ProjectileRoute;

public class DarkWizardCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!ProjectileRoute.hasLineOfSight(entity, target)) {
            return false;
        }
        entity.performGraphic(new Graphic(96, GraphicHeight.HIGH));
        entity.animate(entity.attackAnimation());
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 97, 51, duration, 43, 31, 16, 1, 10);
        final int delay = (int) (p.getSpeed() / 20D);
        entity.executeProjectile(p);
        new Hit(entity, target, delay, this).checkAccuracy(true).submit();
        target.graphic(98, GraphicHeight.HIGH, p.getSpeed());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
