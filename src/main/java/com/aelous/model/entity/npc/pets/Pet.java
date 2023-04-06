package com.aelous.model.entity.npc.pets;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author Ynneh | 20/04/2022 - 19:27
 * <https://github.com/drhenny>
 */
public class Pet {


    /**
     * Check The Pet
     * Pet Type
     * Does the pet have an owner
     * who is the owner
     * is the pet spawned
     * did the player unlock the pet
     */



    /**
     * References the pet for checking bonuses ect
     * @param player
     * @param defs
     * @return
     */
    public static boolean isOut(Player player, PetDefinitions defs) {
        return player.getPet().hasPet() && player.getPet().getCurrentPet().id() == defs.npc;
    }

    private final Player owner;

    public List<Integer> unlockedPets = Lists.newArrayList();
    private NPC currentPet;

    public NPC getCurrentPet() {
        return currentPet;
    }

    public int getCurrentPetAsId() {
        if (!hasPet()) {
            return -1;
        }
        return PetDefinitions.getByNpc(currentPet.id()).item;
    }

    public boolean hasPet() {
        return currentPet != null;
    }

    public Pet(Player owner) {
        this.owner = owner;
    }

    public void onLogin() {
        if (hasPet()) {
            return;
        }
        if (!hasPet()) {
            return;
        }
        spawn(owner.lastPetId, true);//TODO
    }

    public boolean spawn(int itemId, boolean login) {

        PetDefinitions defs = PetDefinitions.getPetByItem(itemId);

        if (defs == null || !owner.getInventory().contains(itemId) && !login) {
            return false;
        }

        //if (!unlockedPets.contains(itemId)) {
        //    owner.getPacketSender().sendMessage("hasn't unlocked.. TODO msg");
        //    return;
        //}

        if (!login) {
            owner.getInventory().remove(itemId);
            /**
             * TODO add pickup/drop animation for pet.
             */
        }

        this.currentPet = new NPC(defs.npc, owner.tile(), true).walkRadius(-1);
        this.followOwner();
        return true;

    }

    public void followOwner() {
        if (!hasPet()) {
            System.err.println("owner doesn't have a pet..");
            return;
        }
        var player = owner;
        var npc = currentPet;
        Chain.bound(null).name("petFollowTask").repeatingTask(1, t -> {
            if (player.isRegistered() && npc.isRegistered()) {
                if (player.dead() ) {
                    return;
                }
                if (!npc.tile().isWithinDistance(player.tile(), 8)) {
                    npc.teleport(player.getAbsX(), player.getAbsY(), player.getZ());
                    return;
                }
                npc.faceEntity(player);

                // path to the previous tick target
                int[] thisTickTarget = {-1, -1};

                DumbRoute.step(npc, player, 1);

                thisTickTarget[0] = npc.getRouteFinder().routeEntity.finishX;
                thisTickTarget[1] = npc.getRouteFinder().routeEntity.finishY;
                npc.getMovement().reset();

                // execute later route
                DumbRoute.step(npc, thisTickTarget[0], thisTickTarget[1]);
            } else {
                npc.remove();
                t.stop();
            }
        });
    }

    public void pickup(boolean logout) {

        World.getWorld().unregisterNpc(currentPet);

        if (logout) {
            return;
        }
        this.owner.getInventory().add(PetDefinitions.getByNpc(currentPet.id()).item, 1);
        this.owner.getPacketSender().sendMessage("You pick up your pet.");
        currentPet = null;
    }

    public boolean unlock() {
        /**
         * TODO
         */
        return false;
    }
}
