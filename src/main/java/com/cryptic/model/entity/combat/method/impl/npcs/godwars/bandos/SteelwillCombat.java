package com.cryptic.model.entity.combat.method.impl.npcs.godwars.bandos;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class SteelwillCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(6154);
        entity.graphic(1216);
        var tileDist = entity.getCentrePosition().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1217, 51, duration, 43, 31, 0, entity.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        target.graphic(hit.getDamage() > 0 ? 166 : 85, GraphicHeight.MIDDLE, p.getSpeed());
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
