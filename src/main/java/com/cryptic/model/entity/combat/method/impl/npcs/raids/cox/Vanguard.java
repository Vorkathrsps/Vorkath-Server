package com.cryptic.model.entity.combat.method.impl.npcs.raids.cox;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @Author Origin
 * @Since October 29, 2021
 */
public class Vanguard extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.isNpc()) {
            NPC npc = entity.getAsNpc();

            if (npc.id() == VANGUARD_7527) {
                meleeAttack(npc, target);
            } else if (npc.id() == VANGUARD_7528) {
                rangeAttack(npc, target);
            } else if (npc.id() == VANGUARD_7529) {
                magicAttack(npc, target);
            }
        }
        return true;
    }

    private void meleeAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
    }

    private void rangeAttack(Entity entity, Entity target) {
        entity.animate(7446);
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        //First attack always targets the player
        new Projectile(entity, target, 1332, 20, 12 * tileDist, 35, 30, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
       // target.delayedGraphics(305, GraphicHeight.HIGH, delay);

        //Handle the second projectile
        var proj_two = new Tile(target.tile().x, target.tile().y + World.getWorld().random(2));
        new Projectile(entity.tile().transform(1, 1, 0), proj_two, 1, 1332, 100, 30, 50, 6, 0).sendProjectile();

        Chain.bound(null).runFn(7, () -> {
            World.getWorld().tileGraphic(305, proj_two, 5, 0);
            if (target.tile().equals(proj_two)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 0, CombatType.RANGED).checkAccuracy(true).submit();
            }
        });
    }

    private void magicAttack(Entity entity, Entity target) {
        entity.animate(7436);
        var tileDist = entity.tile().transform(1, 1, 0).distance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        //First attack always targets the player
        new Projectile(entity, target, 1331, 20, 12 * tileDist, 35, 30, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
      //  target.delayedGraphics(659, GraphicHeight.HIGH, delay);

        //Handle the second projectile
        var proj_two = new Tile(target.tile().x, target.tile().y + World.getWorld().random(2));
        new Projectile(entity.tile().transform(1, 1, 0), proj_two, 1, 1331, 100, 30, 50, 6, 0).sendProjectile();

        Chain.bound(null).runFn(7, () -> {
            World.getWorld().tileGraphic(659, proj_two, 5, 0);
            if (target.tile().equals(proj_two)) {
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy(true).submit();
            }
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return entity.getAsNpc().id() == VANGUARD_7527 ? 1 : 8;
    }
}
