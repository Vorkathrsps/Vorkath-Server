package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.World;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.security.SecureRandom;

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
                if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target))
                    primary_melee_attack(npc, target);
                else primate_ranged_attack(npc, target);
            }
        }
        return true;
    }

    private void spawn_destructive_minions(Entity target) {
        NPC spawn = new NPC(6768, new Tile(target.tile().x + Utils.random(2), target.tile().y + Utils.random(2)));
        spawn.respawns(false);
        spawn.noRetaliation(true);
        spawn.getCombatInfo(World.getWorld().combatInfo(6768));
        spawn.getCombat().setTarget(target);
        spawn.setPositionToFace(target.tile());
        World.getWorld().registerNpc(spawn);

        Chain.runGlobal(4, () -> {
            spawn.animate(7159);
            spawn.stopActions(true);
        }).then(2, () -> {
            spawn.getMovementQueue().clear();
            spawn.hidden(true);
            World.getWorld().tileGraphic(1295, spawn.tile(), 1, 30);

            World.getWorld().getPlayers().forEach(p -> {
                if (p.tile().inSqRadius(spawn.tile(), 1))
                    p.hit(spawn, Utils.random(10));
            });
        }).then(2, () -> World.getWorld().unregisterNpc(spawn));
    }

    private void primary_melee_attack(NPC npc, Entity target) {
        target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        npc.animate(npc.attackAnimation());
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    private void primate_ranged_attack(NPC npc, Entity target) {
        int tileDist = npc.tile().transform(1, 1, 0).distance(target.tile());
        int duration = (41 + -5 + (5 * tileDist));
        npc.animate(7193);
        Projectile p1 = new Projectile(entity, target, 1291, 41, duration, 80, 36, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p1);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();
        hit.submit();
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    private void jump_attack(NPC npc, Entity target) {
        var jump_destination = target.tile();

        npc.animate(7152);
        npc.lockNoDamage();
        Chain.bound(null).name("LizardShamanJumpAttackTask").runFn(3, () -> {
            npc.hidden(true);// removes from client view
            npc.teleport(jump_destination);// just sets new location, doesn't do any npc updating changes (npc doesn't support TELEPORT like players do)
        }).then(2, () -> {
            npc.animate(6946);
            npc.hidden(false);
            npc.setPositionToFace(target.tile());
            npc.unlock();
            npc.getCombat().attack(target);
            if (target.tile().inSqRadius(jump_destination, 3))
                target.hit(npc, World.getWorld().random(25));
        });
    }

    private void green_acidic_attack(NPC npc, Entity target) {
        var green_acidic_orb = new Tile(target.tile().x, target.tile().y);
        var tileDist = npc.tile().distance(target.tile());
        int duration = (51 + 11 + (10 * tileDist));

        npc.animate(7193);
        Projectile p1 = new Projectile(entity, target, 1293, 51, duration, 43, 0, 0, target.getSize(), 10);
        final int delay1 = entity.executeProjectile(p1);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay1, CombatType.MAGIC).checkAccuracy();
        hit.submit();

        Chain.bound(entity).runFn(4, () -> {
            if (target.tile().inSqRadius(green_acidic_orb, 1)) {
                target.hit(npc, Utils.random(30), CombatType.RANGED).submit();
                if (!Poison.poisoned(target)) {
                    if (!CombatFactory.fullShayzien(target)) {
                        if (Utils.securedRandomChance(0.50)) {
                            target.poison(10);
                        }
                    }
                }
            }
        });
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
        World.getWorld().tileGraphic(1294, green_acidic_orb, GraphicHeight.LOW.ordinal(), p1.getSpeed());
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
