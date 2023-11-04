package com.cryptic.model.entity.combat.method.impl.npcs.bosses;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;

import java.security.SecureRandom;

public class Skotizo extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int roll = new SecureRandom().nextInt(5);

        //10% chance that the wold boss skulls you!
        if (World.getWorld().rollDie(10, 1)) {
            Skulling.assignSkullState(((Player) target), SkullType.WHITE_SKULL);
            target.message("The " + entity.getMobName() + " has skulled you, be careful!");
        }

        NPC npc = (NPC) entity;
        if (roll == 1) {
            //System.out.println("rolled one, using ranged attack.");
            ranged_attack(npc, target);
        } else if (roll == 2) {
            //System.out.println("rolled two, using magic attack.");
            magic_attack(npc, target);
        } else {
            if (withinDistance(1)) {
                // System.out.println("melee distance, using melee attack.");
                melee_attack(npc, target);
            } else if (Utils.rollDie(2, 1)) {
                magic_attack(npc, target);
                // System.out.println("Otherwise rolled magic attack due to out of melee distance.");
            } else {
                ranged_attack(npc, target);
                // System.out.println("Otherwise rolled ranged attack due to out of melee distance.");
            }
        }
        return true;
    }

    /**
     * Handles the melee attack
     */
    private void melee_attack(NPC npc, Entity target) {
        Tile tile = target.tile();

        World.getWorld().getPlayers().forEach(player -> {
            if (tile.inSqRadius(player.tile(), 2)) {
                //Wild boss, should deal damage regardless of prayer and don't check attack accuracy makes it harder!
                target.hit(npc, CombatFactory.calcDamageFromType(npc, player, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
            }
        });

        npc.animate(npc.attackAnimation());
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    /**
     * Handles the magic attack
     */
    private void magic_attack(NPC npc, Entity target) {
        Tile tile = target.tile();

        World.getWorld().getPlayers().forEach(player -> {
            if (tile.inSqRadius(player.tile(), 3)) {
                int tileDist = npc.tile().transform(3, 3, 0).distance(player.tile());
                int duration = (51 + -5 + (10 * tileDist));
                Projectile p = new Projectile(entity, target, 165, 51, duration, 80, 30, 0, target.getSize(), 10);
                final int delay = entity.executeProjectile(p);
                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
                hit.submit();
                target.hit(npc, CombatFactory.calcDamageFromType(npc, player, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
                player.graphic(166, GraphicHeight.HIGH, p.getSpeed());
            }
        });
        npc.animate(69);
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }


    /**
     * Handles the ranged attack
     */
    private void ranged_attack(NPC npc, Entity target) {
        Tile tile = target.tile();

        World.getWorld().getPlayers().forEach(player -> {
            if (tile.inSqRadius(player.tile(), 3)) {
                int tileDist = npc.tile().transform(3, 3, 0).distance(player.tile());
                int duration = (41 + 11 + (5 * tileDist));
                Projectile p = new Projectile(npc, player, 1242, 41, duration, 43, 31, 0, player.getSize(), 5);
                final int delay = npc.executeProjectile(p);
                target.hit(npc, CombatFactory.calcDamageFromType(npc, player, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
                player.graphic(1243, GraphicHeight.HIGH, p.getSpeed());
            }
        });

        npc.animate(69);
        npc.getTimers().register(TimerKey.COMBAT_ATTACK, npc.getCombatInfo().attackspeed);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }
}
