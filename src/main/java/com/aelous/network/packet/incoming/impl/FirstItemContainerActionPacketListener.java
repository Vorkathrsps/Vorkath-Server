package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.content.packet_actions.interactions.container.FirstContainerAction;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FirstItemContainerActionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int interfaceId = packet.readInt();
        int slot = packet.readShortA();
        int id = packet.readShortA();

        if (player == null || player.dead()) {
            return;
        }

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (GameServer.properties().debugMode && player.getPlayerRights().isAdministrator(player)) {
            player.debugMessage(String.format("first action, container: %d slot: %d id %d", interfaceId, slot, id));
        }

        FirstContainerAction.firstAction(player, interfaceId, slot, id);
        player.getInventory().refresh();
    }
}
