package com.cryptic.model.entity.combat.method.impl.npcs.misc.revenants;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

public class RevenantMaledictusCombat extends CommonCombatMethod {
    int[] animations = new int[]{9278, 9279};
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity == null || entity.dead()) {
            return false;
        }

        if (!withinDistance(10)) {
            return false;
        }

        if (entity instanceof NPC npc) {
            if (Utils.rollDie(25, 1)) {
                windWave(npc, target);
            } else {
                iceOrBlood(npc, target);
            }
        }

        return true;
    }

    void windWave(Entity npc, Entity target) {
        int bigWave = 134;
        int littleWave = 2034;
        npc.animate(9277);

        Tile targetTile = target.tile().copy();
        var tileDist = npc.tile().distance(targetTile);
        int duration = (58 + 3 + (5 * tileDist));
        var npcTile = npc.tile();

        List<Tile> cornerTiles = new ArrayList<>();
        List<Tile> outlineTiles = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                cornerTiles.add(targetTile.transform(dx, dy));
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if ((Math.abs(dx) == 2 || Math.abs(dy) == 2) && (dx != 2 || dy != 2) && (dx != 2 || dy != -2) && (dx != -2 || dy != 2) && (dx != -2 || dy != -2)) {
                    outlineTiles.add(targetTile.transform(dx, dy));
                }
            }
        }

        Projectile p = new Projectile(npcTile, targetTile, 1456, 58, duration, 125, 8, 16, 5, 208, 5);
        int delay = p.send(npc, targetTile);

        var clientTimePerTick = 20;

        World.getWorld().tileGraphic(bigWave, targetTile, 0, (int) ((delay * clientTimePerTick) * 1.1));

        for (Tile cornerTile : cornerTiles) {
            World.getWorld().tileGraphic(littleWave, cornerTile, 0, (int) (((delay + 1) * clientTimePerTick) * 1.1 * .9));
        }

        for (Tile outlineTile : outlineTiles) {
            World.getWorld().tileGraphic(littleWave, outlineTile, 0, (int) (((delay + 2) * clientTimePerTick) * 1.1 * .9));
        }

        World.getWorld().tileGraphic(bigWave, targetTile, 0, (int) (((delay + 2) * clientTimePerTick) * 1.1 * .9));

        var damage = Utils.random(1, 25);

        Chain.noCtx().runFn(delay, () -> {
            if (target.tile().inSqRadius(targetTile, 2)) {
                if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
                    target.hit(npc, damage / 2);
                } else {
                    target.hit(npc, damage);
                }
            }
        });

        cornerTiles.clear();
        outlineTiles.clear();
    }

    void iceOrBlood(Entity npc, Entity target) {
        var randomAnimation = Utils.randomElement(animations);
        npc.animate(randomAnimation);
        int tileDist = npc.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        var tile = npc.tile().translateAndCenterNpcPosition(npc, target);
        Projectile p = new Projectile(tile, target, 2033, 41, duration, 40, 36, 15, 5, 5);
        final int delay = npc.executeProjectile(p);
        var damage = (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) ? Utils.random(1, 15) : Utils.random(1, 30);
        if (npc.getAnimation().getId() == animations[1]) {
            target.hit(npc, damage, delay);
            if (damage > 1) {
                npc.healHit(npc, damage / 2);
            }
        } else if (npc.getAnimation().getId() == animations[0]) {
            target.hit(npc, damage, delay);
            if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC) && !target.frozen()) {
                target.freeze(15, npc, false);
            }
        }
    }

    @Override
    public void doFollowLogic() {
        if (!withinDistance(5) && !entity.frozen()) {
            var tile = target.tile().transform(1, 1, 0);
            entity.getMovement().step(tile.getX(), tile.getY(), MovementQueue.StepType.REGULAR);
        }
        follow(5);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 5;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        return super.customOnDeath(hit);
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
