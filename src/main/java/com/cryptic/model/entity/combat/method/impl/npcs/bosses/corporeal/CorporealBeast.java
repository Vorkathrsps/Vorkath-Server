package com.cryptic.model.entity.combat.method.impl.npcs.bosses.corporeal;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class CorporealBeast extends CommonCombatMethod {
    int attackCount = 0;

    @Override
    public void init(NPC npc) {
        if (npc.tile().region() == 11844)
            npc.getCombatInfo().aggroradius = 50; // override agro distance to cover the entire region, region specific
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 50);
        npc.useSmartPath = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(15)) {
            return false;
        }

        if (entity == null || target == null) {
            return false;
        }

        attackCount++;

        var corp = (NPC) entity;
        var player = (Player) target;

        BooleanSupplier withinTile = () -> withinDistance(1);
        BooleanSupplier nullTarget = () -> corp.getCombat().getTarget() == null;

        if (attackCount >= Utils.random(3, 6)) {
            magic(corp, player);
            attackCount = 0;
        } else {
            if (withinDistance(1) && Utils.rollDie(5, 1)) {
                if (!withinDistance(1)) {
                    corp.step(player.tile().getX(), player.tile().getY(), MovementQueue.StepType.REGULAR);
                    corp.waitUntil(withinTile, () -> melee(corp, player)).cancelWhen(nullTarget);
                } else {
                    melee(corp, player);
                }
            } else {
                blast(corp, player);
            }
        }

        return true;
    }

    void melee(NPC corp, Player target) {
        int[] animations = new int[]{1682, 1683};
        var randomAnimation = Utils.randomElement(animations);
        corp.animate(randomAnimation);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    void magic(NPC corp, Player target) {
        corp.animate(1680);
        var tileDist = corp.tile().distance(target.tile());
        var duration = (21 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 314, 21, duration, 47, 31, 10, 5 * 64, 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
        drainStats(target);
    }

    void blast(NPC corp, Player target) {
        corp.animate(1680);

        List<Tile> radius = new ArrayList<>();

        var tileCopy = target.tile().copy();

        var tileDist = corp.tile().distance(target.tile());

        var duration = (21 + -5 + (10 * tileDist));

        Projectile p = new Projectile(corp, tileCopy, 316, 21, duration, 47, 31, 10, 5 * 64, 10);

        final int delay = corp.executeProjectile(p);

        for (int index = 0; index < Utils.random(4, 6); index++) {
            var tiles = World.getWorld().randomTileAround(tileCopy, 4);
            radius.add(tiles);
        }

        final Projectile[] blast = {null};

        Chain.noCtx().runFn(delay, () -> {
            if (target.tile().equals(p.getEnd())) {
                target.hit(corp, CombatFactory.calcDamageFromType(corp, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
            }
        });

        Chain.noCtx().runFn(delay / 20 + 2, () -> {
            radius
                .stream()
                .filter(tile -> World.getWorld().clipAt(tile.x, tile.y, tile.level) == 0)
                .forEach(tile -> {

                    blast[0] = new Projectile(tileCopy, tile, 315, 42, duration, 0, 0, 10, 1, 10);

                    blast[0].send(tileCopy, tile);

                    World.getWorld().tileGraphic(1836, tile, 0, blast[0].getSpeed());

                    Chain.noCtx().runFn(blast[0].getSpeed(), () -> {
                        if (target.tile().equals(blast[0].getEnd())) {
                            target.hit(corp, Utils.random(5, 10));
                        }
                        radius.clear();
                    });
                });
        });
    }

    void core(NPC corp, NPC core, Player target) {

    }

    void drainStats(Player target) {
        var reduction = Utils.random(3);
        var magicDrain = target.getSkills().level(Skills.MAGIC) - reduction;
        var prayerDrain = target.getSkills().level(Skills.PRAYER) - reduction;
        if (Utils.rollDie(50, 1)) {
            if (target.getSkills().level(Skills.MAGIC) < reduction) {
                target.getSkills().setLevel(Skills.MAGIC, 0);
            } else {
                target.getSkills().setLevel(Skills.MAGIC, magicDrain);
                target.message("Your Magic has been slightly drained.");
            }

            if (target.getSkills().level(Skills.PRAYER) < reduction) {
                target.getSkills().setLevel(Skills.PRAYER, 0);
            } else {
                target.getSkills().setLevel(Skills.PRAYER, prayerDrain);
                target.message("Your Prayer has been slightly drained.");
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 64;
    }

    @Override
    public void doFollowLogic() {
        DumbRoute.step(entity, target, 1);
    }

}
