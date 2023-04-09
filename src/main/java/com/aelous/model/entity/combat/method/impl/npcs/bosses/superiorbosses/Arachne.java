package com.aelous.model.entity.combat.method.impl.npcs.bosses.superiorbosses;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

public class Arachne extends CommonCombatMethod {

    private void rangedAttack() {
        entity.animate(5322);
        // Throw a ranged projectile
        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
        Projectile projectile = new Projectile(entity, target, 1379, 20,12 * tileDist, 10, 10, 0);
        projectile.sendProjectile();
        var delay = Math.max(1, (20 + (tileDist * 12)) / 30);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
    }

    private void magicAttack() {
        entity.animate(5322);
        // Throw a magic projectile
        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
        Projectile projectile = new Projectile(entity, target, 1380, 20,12 * tileDist, 10, 10, 0);
        projectile.sendProjectile();
        var delay = Math.max(1, (20 + (tileDist * 12)) / 30);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
    }

    private void meleeAttack() {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
    }

    private void webAttack() {
        Tile[] positions = {target.tile().copy(),
            new Tile(entity.getAbsX() + Utils.random(-4, entity.getSize() + 4), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ()),
            new Tile(entity.getAbsX() + Utils.random(-4, entity.getSize() + 4), entity.getAbsY() + Utils.random(-4, entity.getSize() + 4), entity.getZ())};
        for (Tile pos : positions) {
            entity.runFn(1, () -> World.getWorld().tileGraphic(1601, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0)).then(2, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity, World.getWorld().random(10, 15));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity, 7);
                }
            }).then(2, () -> World.getWorld().tileGraphic(1601, new Tile(pos.getX(), pos.getY(), pos.getZ()), 0, 0)).then(1, () -> {
                if (target == null)
                    return;
                if (target.tile().equals(pos)) {
                    target.hit(entity,World.getWorld().random(10, 18));
                } else if (Utils.getDistance(target.tile(), pos) == 1) {
                    target.hit(entity,10);
                }
            });
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollPercent(25)) {
            meleeAttack();
        } else if (Utils.rollPercent(50)) {
            rangedAttack();
        } else if (Utils.rollPercent(10)) {
            webAttack();
        } else {
            magicAttack();
        }
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
