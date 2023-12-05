package com.cryptic.model.entity.combat.method.impl.npcs.bosses;

import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

import java.security.SecureRandom;
import java.util.function.BooleanSupplier;

public class LizardShaman extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var random = new SecureRandom().nextInt(5);
        NPC npc = (NPC) entity;


        switch (random) {
            case 1 -> jump_attack(npc, target);
            case 2 -> spawn_destructive_minions(target);
            case 3 -> green_acidic_attack(npc, target);
            default -> {
                if (withinDistance(1))
                    primary_melee_attack(npc, target);
                else primate_ranged_attack(npc, target);
            }
        }
        return true;
    }

    private void spawn_destructive_minions(Entity target) {
        NPC spawn = new NPC(6768, new Tile(target.tile().x + Utils.random(2), target.tile().y + Utils.random(2)));
        spawn.respawns(false).noRetaliation(true);
        spawn.setCombatInfo(World.getWorld().combatInfo(6768));
        spawn
            .spawn(false)
            .setPositionToFace(target.tile())
            .getCombat()
            .setTarget(target);
        BooleanSupplier isViewable = () -> !target.tile().isViewableFrom(spawn.tile());
        Chain.noCtx().runFn(4, () -> {
            spawn.animate(7159);
            spawn.stopActions(true);
        }).then(3, () -> {
            spawn.getMovementQueue().clear();
            spawn.remove();
            World.getWorld().tileGraphic(1295, spawn.tile(), 1, 0);
            if (target.tile().inSqRadius(spawn.tile(), 1)) {
                target.hit(entity, Utils.random(1, 10));
            }
        }).cancelWhen(isViewable).then(1, spawn::remove);
    }

    private void primary_melee_attack(NPC npc, Entity target) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        npc.animate(npc.attackAnimation());
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    private void primate_ranged_attack(NPC npc, Entity target) {
        int tileDist = npc.tile().transform(1, 1, 0).distance(target.tile());
        int duration = (41 + -5 + (5 * tileDist));
        npc.animate(7193);
        Projectile p1 = new Projectile(entity, target, 1291, 41, duration, 80, 36, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p1);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);
        hit.submit();
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    private void jump_attack(NPC npc, Entity target) {
        var jump_destination = target.tile().copy();

        npc.animate(7152);
        npc.lockNoDamage();
        BooleanSupplier notViewableFromTile = () -> !jump_destination.isViewableFrom(entity.tile());
        Chain.noCtx().runFn(2, () -> {
            npc.hidden(true);
            npc.teleport(jump_destination);
        }).then(2, () -> {
            npc.hidden(false);
            npc.animate(6946);
            npc.setPositionToFace(target.tile());
            npc.unlock();
            npc.getCombat().setTarget(target);
            if (target.tile().inSqRadius(jump_destination, 1))
                 entity.submitHit(target, 1, this)
                    .checkAccuracy(false)
                    .setDamage(Utils.random(1, 25))
                    .submit();
        }).cancelWhen(notViewableFromTile).then(1, () -> {
            npc.hidden(false);
            npc.unlock();
        });
    }

    private void green_acidic_attack(NPC npc, Entity target) {
        var green_acidic_orb = new Tile(target.tile().x, target.tile().y);
        var copiedTile = target.tile().copy();
        var tileDist = npc.tile().distance(copiedTile);
        int duration = (51 + 11 + (10 * tileDist));
        npc.animate(7193);
        Projectile projectile = new Projectile(entity, copiedTile, 1293, 51, duration, 43, 0, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(projectile);
        Chain.noCtx().runFn(delay, () -> {
            if (!target.tile().equals(copiedTile)) return;
            entity.submitHit(target, 0, this)
                .checkAccuracy(false)
                .setDamage(Utils.random(1, 30))
                .submit()
                .postDamage(hit -> {
                    if (!hit.isAccurate()) {
                        hit.block();
                        return;
                    }
                    if (!Poison.poisoned(target)) {
                        if (!CombatFactory.fullShayzien(target)) {
                            if (Utils.securedRandomChance(0.50)) {
                                target.poison(10);
                            }
                        }
                    }
                });
        });
        World.getWorld().tileGraphic(1294, green_acidic_orb, GraphicHeight.LOW.ordinal(), projectile.getSpeed());
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
