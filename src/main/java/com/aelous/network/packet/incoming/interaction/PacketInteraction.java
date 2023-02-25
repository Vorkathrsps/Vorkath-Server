package com.aelous.network.packet.incoming.interaction;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;

/**
 * Represents the packet interaction
 *
 * @author 2012
 *
 */
public abstract class PacketInteraction {

    public boolean handleEquipment(Player player, Item item) { return false;}

    public boolean handleEquipmentAction(Player player, Item item, int slot) { return false;}

    /**
     * Handles button interaction
     *
     * @param player
     *            the player
     * @param button
     *            the button
     * @return packet interaction
     */
    public boolean handleButtonInteraction(Player player, int button) {
        return false;
    }

    /**
     * Handles item interaction
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param option
     *            the type
     * @param option
     *            the type
     * @return the interaction
     */
    public boolean handleItemInteraction(Player player, Item item, int option) {
        return false;
    }

    /**
     * Handles object interaction
     *
     * @param player
     *            the player
     * @param object
     *            the object
     * @param option
     *            the type
     * @return the interaction
     */
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        return false;
    }

    /**
     * Handle npc interaction
     *
     * @param player
     *            the player
     * @param npc
     *            the npc
     * @param option
     *            the type
     * @return the interaction
     */
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        return false;
    }

    /**
     * Handles item use on entity interaction
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param target
     *            the entity
     * @return the interaction
     */
    public boolean handleItemOnPlayer(Player player, Item item, Player target) {
        return false;
    }

    /**
     * Handles item on item interaction
     *
     * @param player
     *            the player
     * @param use
     *            the use item on
     * @param usedWith
     *            the item used on
     * @return the interaction
     */
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        return false;
    }

    /**
     * Handles item on object interaction
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param object
     *            the object
     * @return the interaction
     */
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        return false;
    }

    public boolean handleItemOnNpc(Player player, Item item, NPC npc) {
        return false;
    }

    /**
     * Handles item container interaction action
     *
     * @param player
     *            the player
     * @param item
     *            the item
     * @param slot
     *            the slot
     * @param interfaceId
     *            the interface id
     * @param type
     *            the type
     * @return the interaction
     */
    public boolean handleItemContainerActionInteraction(Player player, Item item, int slot, int interfaceId, int type) {
        return false;
    }
}
