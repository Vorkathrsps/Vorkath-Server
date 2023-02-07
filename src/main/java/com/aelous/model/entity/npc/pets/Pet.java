package com.aelous.model.entity.npc.pets;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author Ynneh | 20/04/2022 - 19:27
 * <https://github.com/drhenny>
 */
public class Pet {

    /**
     * References the pet for checking bonuses ect
     * @param player
     * @param defs
     * @return
     */
    public static boolean isOut(Player player, PetDefinitions defs) {
        return player.getPet().hasPet() && player.getPet().currentPet.id() == defs.npc;
    }

    private Player owner;

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
            System.err.println("already has a pet???");
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

        NPC npc = new NPC(defs.npc, owner.tile(), true).walkRadius(-1);
        currentPet = npc;
        followOwner();
        return true;

    }

    public void followOwner() {
        if (!hasPet()) {
            System.err.println("owner doesn't have a pet..");
            return;
        }
        currentPet.getMovement().follow(owner);

    }

    public void pickup(boolean logout) {

        World.getWorld().unregisterNpc(currentPet);

        if (logout) {
            return;
        }
        owner.getInventory().add(PetDefinitions.getByNpc(currentPet.id()).item, 1);
        owner.getPacketSender().sendMessage("You pick up your pet.");
        currentPet = null;
    }

    public boolean unlock() {
        /**
         * TODO
         */
        return false;
    }
}
