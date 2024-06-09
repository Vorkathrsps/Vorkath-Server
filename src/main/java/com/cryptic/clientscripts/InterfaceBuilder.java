package com.cryptic.clientscripts;

import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.region.RegionManager;
import com.displee.cache.index.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to build and manage game interfaces.
 *
 * @Author: Origin
 * @Date: 6/8/24
 */
public abstract class InterfaceBuilder {
    ArrayList<EventNode> events = new ArrayList<>();

    /**
     * Creates and returns the game interface.
     *
     * @return the game interface.
     */
    public abstract GameInterface gameInterface();

    /**
     * Operations to perform before opening the interface.
     *
     * @param player the player for whom the interface is being opened.
     */
    public abstract void beforeOpen(final Player player);

    /**
     * Handles button clicks on the interface.
     *
     * @param player the player who clicked the button.
     * @param button the button that was clicked.
     * @param option the option selected on the button.
     * @param slot the slot where the button is located.
     * @param itemId the ID of the item associated with the button.
     */
    public void onButton(Player player, final int button, final int option, final int slot, final int itemId) {

    }

    /**
     * Determines whether to send the interface to the player.
     *
     * @return true if the interface should be sent, false otherwise.
     */
    protected boolean sendInterface() {
        return true;
    }

    /**
     * Opens the interface for the player.
     *
     * @param player the player for whom the interface is being opened.
     */
    public void open(final Player player) {
        GameInterface gameInterface = gameInterface();
        int interfaceId = gameInterface.getId();
        player.activeInterface.put(interfaceId, this);
        beforeOpen(player);
        initialize(player);
        if (sendInterface()) {
            player.interfaces.sendInterface(gameInterface);
        }
    }

    /**
     * Handles resuming pause action on the interface.
     *
     * @param player the player resuming pause.
     * @param slot the slot associated with the action.
     */
    public void onResumePause(Player player, final int slot) {
        // Method implementation
    }

    /**
     * Handles targeting an NPC.
     *
     * @param player the player targeting the NPC.
     * @param selectedButton the button used for targeting.
     * @param selectedSub the sub-option selected.
     * @param selectedItemId the ID of the selected item.
     * @param target the NPC being targeted.
     */
    public void onTargetNpc(Player player, final int selectedButton, final int selectedSub, final int selectedItemId, NPC target) {
        // Method implementation
    }

    /**
     * Handles targeting an object.
     *
     * @param player the player targeting the object.
     * @param selectedButton the button used for targeting.
     * @param selectedSub the sub-option selected.
     * @param selectedItemId the ID of the selected item.
     * @param targetedObject the object being targeted.
     */
    public void targetObject(Player player, int selectedButton, int selectedSub, int selectedItemId, GameObject targetedObject) {
        // Method implementation
    }

    /**
     * Handles targeting a ground item.
     *
     * @param player the player targeting the ground item.
     * @param selectedButton the button used for targeting.
     * @param selectedSub the sub-option selected.
     * @param selectedItemId the ID of the selected item.
     * @param targetedItem the ground item being targeted.
     */
    public void targetGroundItem(Player player, int selectedButton, int selectedSub, int selectedItemId, GroundItem targetedItem) {
        // Method implementation
    }

    /**
     * Handles targeting a button on the interface.
     *
     * @param player the player targeting the button.
     * @param selectedButton the button used for targeting.
     * @param selectedSlot the slot of the button.
     * @param selectedItemId the ID of the selected item.
     * @param targetButton the targeted button.
     * @param targetSlot the slot of the targeted button.
     * @param targetItemId the ID of the targeted item.
     */
    public void onTargetButton(Player player, final int selectedButton, final int selectedSlot, final int selectedItemId, final int targetButton, final int targetSlot, final int targetItemId) {
        // Method implementation
    }

    /**
     * Handles targeting another player.
     *
     * @param player the player targeting another player.
     * @param selectedButton the button used for targeting.
     * @param selectedSub the sub-option selected.
     * @param selectedItemId the ID of the selected item.
     * @param targetedPlayer the player being targeted.
     */
    public void targetPlayer(Player player, int selectedButton, int selectedSub, int selectedItemId, Player targetedPlayer) {
        // Method implementation
    }

    /**
     * Handles dragging action on the interface.
     *
     * @param player the player performing the drag.
     * @param fromButton the button from where the drag starts.
     * @param fromSlot the slot from where the drag starts.
     * @param fromItemId the ID of the item being dragged.
     * @param toButton the button to where the drag ends.
     * @param toSlot the slot to where the drag ends.
     * @param toItemId the ID of the item being dragged to.
     */
    public void onDrag(Player player, final int fromButton, final int fromSlot, final int fromItemId, final int toButton, final int toSlot, final int toItemId) {
        // Method implementation
    }

    /**
     * Handles resuming pause for objects.
     *
     * @param player the player resuming pause.
     * @param id the ID of the object.
     */
    public void onResumePObj(Player player, int id) {
        // Method implementation
    }

    /**
     * Handles closing the modal.
     *
     * @param player the player for whom the modal is being closed.
     */
    public void onModalClosed(Player player) {
        // Method implementation
    }

    /**
     * Closes the interface for the player.
     *
     * @param player the player for whom the interface is being closed.
     */
    public void close(final Player player) {
        onModalClosed(player);
        player.interfaces.closeInterface(gameInterface().getPosition());
        player.activeInterface.remove(gameInterface().getId());

    }

    /**
     * Adds an event node to the list of events.
     *
     * @param node the event node to be added.
     */
    public void setEvents(final EventNode node) {
        if (!this.events.contains(node)) this.events.add(node);
    }

    /**
     * Adds a list of event nodes to the list of events.
     *
     * @param nodes the list of event nodes to be added.
     */
    public void setEvents(final List<EventNode> nodes) {
        if (!this.events.containsAll(nodes)) this.events.addAll(nodes);
    }

    /**
     * Initializes the interface for the player with all event nodes.
     *
     * @param player the player for whom the interface is being initialized.
     */
    public void initialize(final Player player) {
        for (final EventNode node : events) {
            node.interfaceID = gameInterface().getId();
            node.setButtons().setFlags().send(player);
        }
    }

    /**
     * Finds and returns the script ID based on the script name.
     *
     * @param name the name of the script.
     * @return the script ID.
     * @throws RuntimeException if the script cannot be found.
     */
    public static int find(final String name) {
        Index clientscripts = RegionManager.cache.index(12);
        int scriptId = clientscripts.archiveId("[clientscript," + name + "]");
        if (scriptId == -1) {
            throw new RuntimeException("unable to find [clientscript," + name + "]");
        }
        return scriptId;
    }
}
