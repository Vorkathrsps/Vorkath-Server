package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * @author PVE
 * @Since augustus 07, 2020
 */
public class InfernalMageCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        entity.graphic(129, GraphicHeight.HIGH, 0);
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 130, 51, duration, 43, 31, 0, entity.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        target.hit(entity, hit, delay, CombatType.MAGIC).checkAccuracy(true).submit();

        if(hit > 0) {
            target.graphic(131, GraphicHeight.HIGH, p.getSpeed());
        } else {
            target.graphic(85, GraphicHeight.HIGH, p.getSpeed());
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
