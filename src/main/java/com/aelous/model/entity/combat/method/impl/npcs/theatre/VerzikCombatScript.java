package com.aelous.model.entity.combat.method.impl.npcs.theatre;

import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.theatre.nylocas.Nylocas;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;

public class VerzikCombatScript extends CommonCombatMethod implements Nylocas {
    @Override
    public void prepareAttack(Entity entity, Entity target) {
        SecureRandom randomCheck = new SecureRandom();

        NPC npc = (NPC) entity;

        entity.animate(entity.attackAnimation());

            //if (randomCheck.nextDouble() <= 0.10D) {
            //    if (nylosExploding.isEmpty()) {
             //       cast_spawn_exploding_nylos(target);
             //   }
           // }

        /**
         * Randomized Nylo Spawn
         */
            //if (randomCheck.nextDouble() <= 0.08D) {
        //if (nylosRed.isEmpty()) {
               spawnExplodingNylos(target, 5);
        //}

        /**
         * Verziks BodySlam Attack
         */
        if (verzikBodySlamDistanceCheck(entity, target) <= 2) {
            bodySlam(entity, target);
        }
    }

    private int verzikBodySlamDistanceCheck(Entity verzik, Entity target) {
        return verzik.tile().distance(target.tile());
    }

    private void cast_spawn_blood_nylo(NPC npc, Entity verzik) {
        Task.runOnceTask(3, t -> {
            Nylocas.spawnNyloBlood(verzik);
        });
    }

    /**
     * Verziks bodyslam attack
     * @param verzik
     * @param target
     */
    private void bodySlam(Entity verzik, Entity target) {
        Chain.bound(verzik).runFn(0, () -> verzik.animate(8116));
        Chain.bound(target).runFn(0, target::lockDamageOk);
        Chain.bound(target).runFn(4, target::unlock);
        target.hit(target, CombatFactory.calcDamageFromType(verzik, target, CombatType.MELEE), 1, CombatType.MELEE).setAccurate(true).submit();
    }


    /**
     * Method for exploding Nylos
     * @param target
     * @param explodeTick
     */
    private void spawnExplodingNylos(Entity target, int explodeTick) {
        //TODO make an arraylist & iterator to validate theres x amount of nylos already spawned
        //TODO she also tosses a slow purple projectile which can be avoided but if it lands on the tile the player's on it can deal up to 78 damage
        SecureRandom random = new SecureRandom();

        NPC spawnWhite = new NPC(8345, new Tile(target.tile().x + Utils.random(2), target.tile().y + Utils.random(2)));
        NPC spawnBlue = new NPC(8347, new Tile(target.tile().x + Utils.random(2), target.tile().y + Utils.random(2)));
        NPC spawnGreen = new NPC(8352, new Tile(target.tile().x + Utils.random(2), target.tile().y + Utils.random(2)));

        //TODO area check so they do not spawn out of bounds

        spawnWhite.respawns(false);
        spawnBlue.respawns(false);
        spawnGreen.respawns(false);
        spawnWhite.combatInfo(World.getWorld().combatInfo(8345));
        spawnBlue.combatInfo(World.getWorld().combatInfo(8347));
        spawnGreen.combatInfo(World.getWorld().combatInfo(8352));

        /**
         * White Nylo
         */
        Chain.bound(spawnWhite).runFn(0, () -> {
            World.getWorld().registerNpc(spawnWhite);
            nylosExploding.add(spawnWhite);
            spawnWhite.lock();
        }).then(spawnWhite::unlock).then(2, () -> {
            //random.nextInt(target); create an arraylist for the random to choose what the spawn will target
            spawnWhite.getCombat().setTarget(target);
            spawnWhite.getMovementQueue().follow(target);
        }).then(explodeTick, () -> {
            spawnWhite.stopActions(true);
            spawnWhite.animate(8426);
        }).then(2, () -> {
            spawnWhite.hidden(true);
            World.getWorld().getPlayers().forEach(p -> {
                if (p.tile().inSqRadius(spawnWhite.tile(), 1))
                    p.hit(target, random.nextInt(63));
            });
        }).then(2, () -> World.getWorld().unregisterNpc(spawnWhite)).then(0, () -> {
            nylosExploding.remove(spawnWhite);
        });

        /**
         * Blue Nylo
         */
        Chain.bound(spawnBlue).runFn(0, () -> {
            World.getWorld().registerNpc(spawnBlue);
            nylosExploding.add(spawnBlue);
            spawnBlue.lock();
        }).then(spawnWhite::unlock).then(2, () -> {
            //random.nextInt(target); create an arraylist for the random to choose what the spawn will target
            spawnBlue.getCombat().setTarget(target);
            spawnBlue.getMovementQueue().follow(target);
        }).then(explodeTick, () -> {
            spawnBlue.stopActions(true);
            spawnBlue.animate(8426);
        }).then(2, () -> {
            spawnBlue.hidden(true);
            World.getWorld().getPlayers().forEach(p -> {
                if (p.tile().inSqRadius(spawnBlue.tile(), 1))
                    p.hit(target, random.nextInt(63));
            });
        }).then(2, () -> World.getWorld().unregisterNpc(spawnBlue)).then(0, () -> {
            nylosExploding.remove(spawnBlue);
        });

        /**
         * Green Nylo
         */
        Chain.bound(spawnGreen).runFn(0, () -> {
            World.getWorld().registerNpc(spawnGreen);
            nylosExploding.add(spawnGreen);
            spawnGreen.lock();
        }).then(spawnWhite::unlock).then(2, () -> {
            //random.nextInt(target); create an arraylist for the random to choose what the spawn will target
            spawnGreen.getCombat().setTarget(target);
            spawnGreen.getMovementQueue().follow(target);
        }).then(explodeTick, () -> {
            spawnGreen.stopActions(true);
            spawnGreen.animate(8426);
        }).then(2, () -> {
            spawnGreen.hidden(true);
            World.getWorld().getPlayers().forEach(p -> {
                if (p.tile().inSqRadius(spawnGreen.tile(), 1))
                    p.hit(target, random.nextInt(63));
            });
        }).then(2, () -> World.getWorld().unregisterNpc(spawnGreen)).then(0, () -> {
            nylosExploding.remove(spawnGreen);
        });

        if (spawnWhite.dead() && spawnBlue.dead() && spawnGreen.dead()) {
            nylosExploding.clear();
        }
    }

    //NYLOCAS SUMMON: tosses slow purple projectile which can be avoided but if it lands on the tile of the player
    //it can deal UP TO: 78 damage projectile also transforms into nylocas athanatos which will heal her but can be removed
    //by attacking her once, common nylocas will follow a player and explode dealing 63 damage if reached
    //when verziks health gets to 35% she will use blood spells to heal her which will drain players prayer if prayed against,

    @Override
    public int getAttackSpeed(Entity entity) {
        return 5;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 5;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}
