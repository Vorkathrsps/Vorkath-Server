package com.cryptic.model.entity.combat.method.impl.npcs.fossilisland;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.Combat;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.Utils;

public class AncientWyvernCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity wyvern, Entity target) {
        int roll = Utils.random(2);
        if(roll == 1) {
            doMagic(wyvern, target);
        } else {
            if (withinDistance(1)) {
                if (Utils.random(2) == 1) {
                    doMelee(wyvern, target);
                } else {
                    doTailWhip(wyvern, target);
                }
            } else {
                doMagic(wyvern, target);
            }
        }
        return true;
    }

    private void doMagic(Entity entity, Entity target) {
        entity.animate(7658);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 162, 51, duration, 43, 31, 0, target.getSize(), 10);
        int delay = entity.executeProjectile(p);
        target.performGraphic(new Graphic(137, GraphicHeight.HIGH, p.getSpeed()));
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
    }

    private void doTailWhip(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void doMelee(Entity entity, Entity target) {
        entity.animate(7658);
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
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
