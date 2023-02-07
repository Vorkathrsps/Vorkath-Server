package com.aelous.model.entity.combat.method.impl.npcs.theatre.nylocas;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;
import java.util.ArrayList;

public interface Nylocas {

    ArrayList<NPC> nylosRed = new ArrayList<>();
    ArrayList<NPC> nylosExploding = new ArrayList<>();


    /**
     * Method for spawning the healing blood nylo's
     */
    static void spawnNyloBlood(Entity verzik) {
        //TODO make an arraylist & iterator to validate theres x amount of nylos already spawned
        //TODO she also tosses a slow purple projectile which can be avoided but if it lands on the tile the player's on it can deal up to 78 damage

        NPC spawn1 = new NPC(10862, new Tile(verzik.tile().x + Utils.random(3), verzik.tile().y));

            //TODO area check so they do not spawn out of bounds
        spawn1.respawns(false);
        spawn1.noRetaliation(true);
        World.getWorld().registerNpc(spawn1);
        spawn1.getMovementQueue().canMove();

            Chain.runGlobal(
                0, () -> {
                nylosRed.add(spawn1);
                spawn1.animate(8098);
            }).then(20, () -> {
                spawn1.animate(8097);
                spawn1.stopActions(true);
            }).then(0, () -> {
                spawn1.hidden(true);
                nylosRed.remove(spawn1);
            }).then(1, () -> World.getWorld().unregisterNpc(spawn1));

        //verzik.heal(setHealerNyloHitpoints(spawn1));

            /*Chain.bound(spawn2).runFn(0, () -> {
                nylosRed.add(spawn2);
                spawn2.animate(8098);
            }).then(5, () -> {
                spawn2.stopActions(true);
                System.out.println("stopping actions");
                spawn2.animate(8097);
                System.out.println("performing animation");
                verzik.heal(setHealerNyloHitpoints(spawn1));
                System.out.println("grabbing hitpoints");
            }).then(2, () -> {
                spawn2.hidden(true);
            }).then(2, () -> World.getWorld().unregisterNpc(spawn2)).then(0, () -> {
                nylosRed.remove(spawn2);
            });
            if (spawn1.dead() && spawn2.dead()) {
                nylosRed.clear();
            }*/
        }

    /**
     * Getter for current hitpoints of the healing nylos
     * @param npc
     * @return
     */
    default int getCurrentHitPoints(NPC npc) {
        return (int) (npc.hp() * 1.0 / (npc.maxHp() * 1.0));
    }
    default int setHealerNyloHitpoints(NPC npc) {
        return getCurrentHitPoints(npc);
    }

}
