package com.aelous.model.entity.combat.method.impl.npcs.fightcaves;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import javax.annotation.Nonnull;

/**
 * @Author: Origin
 * @Date: 7/14/2023
 */
public class TztokJadCombatScript extends CommonCombatMethod {

    private static final int MAX_DISTANCE = 10;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.dead()) {
            return false;
        }
        if (withinDistance(1)) {
            if (Utils.rollDie(4, 1)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
                entity.animate(entity.attackAnimation());
            } else if (Utils.rollDie(2, 1)) {
                magicAttack(entity, target);
            } else {
                rangeAttack(entity, target);
            }
            return true;
        } else {
            if (Utils.rollDie(2, 1)) {
                magicAttack(entity, target);
            } else {
                rangeAttack(entity, target);
            }
        }
        return true;
    }

    private void magicAttack(@Nonnull Entity entity, @Nonnull Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.animate(2656);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (8 * tileDist));

        final Projectile[] projectileOrder = {
            new Projectile(entity, target, 448, 41, duration, 128, 31, 12, 4, 8),
            new Projectile(entity, target, 449, 45, duration + 15, 128, 31, 12, 4, 8),
            new Projectile(entity, target, 450, 49, duration + 30, 128, 31, 12, 4, 8),
        };

        int delay = entity.executeProjectile(projectileOrder[0]);
        for (int i = 1; i < projectileOrder.length; i++) {
            entity.executeProjectile(projectileOrder[i]);
        }

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        target.graphic(157, GraphicHeight.MIDDLE, projectileOrder[0].getSpeed());
    }

    private void rangeAttack(@Nonnull Entity entity, @Nonnull Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.animate(2652);
        World.getWorld().tileGraphic(451, target.tile(), 0, 60);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 4, CombatType.RANGED).checkAccuracy();
        hit.submit();
        target.graphic(157, GraphicHeight.LOW, 120);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return MAX_DISTANCE;
    }

    @Override
    public void doFollowLogic() {
        follow(MAX_DISTANCE);
    }
}
