package com.cryptic.clientscripts.interfaces;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.region.RegionManager;
import com.displee.cache.index.Index;

import java.util.ArrayList;
import java.util.List;

public abstract class InterfaceBuilder {
    ArrayList<EventNode> events = new ArrayList<>();

    protected GameInterface gameInterface() {
        return null;
    }

    protected boolean sendInterface() {
        return true;
    }

    public void beforeOpen(final Player player) {

    }

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

    public void onResumePause(Player player, final int slot) {
    }

    public void onButton(Player player, final int button, final int option, final int slot, final int itemId) {

    }

    /**
     * @Override public boolean targetPlayer(Player player, int selectedCom, int selectedComSub, int selectedItem, Player targetedPlayer) {
     * return targetNode(player, selectedComSub, selectedItem, targetedPlayer);
     * }
     * @Override public boolean targetObject(Player player, int selectedCom, int selectedComSub, int selectedItem, GameObject targetedObject) {
     * return targetNode(player, selectedComSub, selectedItem, targetedObject);
     * }
     * @Override public boolean targetGroundItem(Player player, int selectedCom, int selectedComSub, int selectedItem, GroundItem targetedItem) {
     * return targetNode(player, selectedComSub, selectedItem, targetedItem);
     * }
     */

    public void onTargetNpc(Player player, final int selectedButton, final int selectedSub, final int selectedItemId, NPC target) {

    }

    public void onTargetButton(Player player, final int selectedButton, final int selectedSlot, final int selectedItemId, final int targetButton, final int targetSlot, final int targetItemId) {

    }

    public void onTargetPlayer(Player player, final int selectedButton, final int selectedSlot, final int selectedItemId, final int targetButton, final int targetSlot, final int targetItemId) {

    }

    public void onDrag(Player player, final int fromButton, final int fromSlot, final int fromItemId, final int toButton, final int toSlot, final int toItemId) {

    }

    public void onResumePObj(Player player, int id) {

    }

    public void onModalClosed(Player player) {

    }

    public void close(final Player player) {
        if (sendInterface()) {
            onModalClosed(player);
            player.activeInterface.remove(gameInterface().getId());
        }
    }

    public void setEvents(final EventNode node) {
        if (!this.events.contains(node)) this.events.add(node);
    }

    public void setEvents(final List<EventNode> nodes) {
        if (!this.events.containsAll(nodes)) this.events.addAll(nodes);
    }

    public void initialize(final Player player) {
        for (final EventNode node : events) {
            node.interfaceID = gameInterface().getId();
            node.setButtons().setFlags().send(player);
        }
    }

    public static int find(final String name) {
        Index clientscripts = RegionManager.cache.index(12);
        int scriptId = clientscripts.archiveId("[clientscript," + name + "]");
        if (scriptId == -1) {
            throw new RuntimeException("unable to find [clientscript," + name + "]");
        }
        return scriptId;
    }
}
