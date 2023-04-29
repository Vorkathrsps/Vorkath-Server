package com.aelous.model.entity.combat.method.impl.npcs.karuulm;

import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.hydra.HydraAttacks;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Patrick van Elderen | December, 22, 2020, 22:49
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class HydraCombatScript extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC npc = (NPC) entity;

        if (npc instanceof Hydra) {
            Hydra hydra = (Hydra) npc;

            if (System.currentTimeMillis() - hydra.lastPoisonPool >= 30000L) {
                sendPoisonAttack(hydra, target);
                hydra.lastPoisonPool = System.currentTimeMillis();
            } else {
                regularAttack(hydra, target);
            }
        }
        return true;
    }

    /**
     * Sends the hydra's ranged or magical attack.
     */
    private void regularAttack(Hydra hydra, Entity target) {
        hydra.animate(hydraAttacks.get(Utils.random(hydraAttacks.size() - 1)));

        hydra.recordedAttacks--;

        if (hydra.recordedAttacks == 0) {
            hydra.currentAttack = hydra.currentAttack == HydraAttacks.MAGIC ? HydraAttacks.RANGED : HydraAttacks.MAGIC;
            hydra.recordedAttacks = 3;
        }

       // hydra.executeProjectile(new Projectile(hydra, target, hydra.currentAttack == HydraAttacks.MAGIC ? 1663 : 1662, 30, speed ,35 ,0,0, hydra.getSize()));
        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, hydra.currentAttack == HydraAttacks.MAGIC ? 1663 : 1662, hydra.currentAttack == HydraAttacks.MAGIC ?  51 : 41, duration, 43, 31, 0, target.getSize(), hydra.currentAttack == HydraAttacks.MAGIC ? 10 : 5);
        final int delay = hydra.executeProjectile(p);

        Chain.bound(null).runFn(delay, () -> target.hit(hydra, CombatFactory.calcDamageFromType(
            hydra, target, hydra.currentAttack ==
            HydraAttacks.MAGIC ? CombatType.MAGIC : CombatType.RANGED),
            hydra.currentAttack == HydraAttacks.MAGIC ? CombatType.MAGIC : CombatType.RANGED)
            .checkAccuracy().submit());
    }

    /**
     * Sends the poison pool attack.
     */
    private void sendPoisonAttack(Hydra hydra, Entity target) {
        hydra.animate(8263);
        List<Tile> targets = new LinkedList<>();
        targets.add(target.tile().copy());
        Area hydraBounds = hydra.bounds();
        List<Tile> positions = target.tile().area(3, pos -> !pos.inArea(hydraBounds) && ProjectileRoute.allow(hydra, pos));
        for (int i = 0; i < 2; i++)
            targets.add(Utils.randomElement(positions));
        targets.forEach(pos -> hydra.runFn(1, () -> {
       //     hydra.executeProjectile(new Projectile(hydra, target, 1, 1644, 54, 25, 35, 0, hydra.getSize()));
            var tileDist = entity.tile().distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p = new Projectile(entity, target, 1644, 41, duration, 43, 31, 0, target.getSize(), 5);
            final int delay = hydra.executeProjectile(p);
            Direction dir = Direction.getDirection(Utils.getClosestTile(hydra, pos), pos);

            World.getWorld().tileGraphic(1645, new Tile(pos.getX(), pos.getY()), pos.getZ(), p.getSpeed());
            World.getWorld().tileGraphic(POISON_POOLS[dir.ordinal()], new Tile(pos.getX(), pos.getY()), pos.getZ(),p.getSpeed());
            Chain.bound(hydra).runFn(3, () -> {
                for (int i = 0; i < 15; i++) {
                    if (target.tile().equals(pos)) {
                        Hit hit = Hit.builder(hydra, target, World.getWorld().random(1, 4), delay, CombatType.RANGED).checkAccuracy();
                        hit.submit();

                    }
                    Chain.bound(hydra).runFn(2, () -> {
                        //Just ticking
                    });
                }
            });
        }));
    }

    private final int[] POISON_POOLS = { // indexed by direction as in Direction class
        1658,
        1659,
        1660,
        1657,
        1661,
        1656,
        1655,
        1654,
    };

    /**
     * Fires a projectile from the hydra to a tile.
     */
    private void fireProjectileToLocation(NPC hydra, Tile tile) {
        var center = Utils.getCenterLocation(hydra);
        var dir = Direction.of(tile.x - center.x, tile.y - center.y);
        var from = center.transform(dir.x * 2, dir.y * 2);
       // new Projectile(from, tile, 1, 1644, 90, 50, 55, 0, 0, 0, 5).sendProjectile();
    }

    /**
     * The hydra's attack animations.
     */
    private final List<Integer> hydraAttacks = List.of(8261, 8262, 8263);

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
