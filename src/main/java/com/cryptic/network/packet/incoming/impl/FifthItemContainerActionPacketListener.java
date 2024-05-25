package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.packet_actions.interactions.container.FifthContainerAction;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FifthItemContainerActionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {

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

        int interfaceId = packet.readInt();
        int slot = packet.readLEShort();
        int id = packet.readLEShort();
        if (GameServer.properties().debugMode && player.getPlayerRights().isCommunityManager(player)) {
            player.debugMessage(String.format("fifth action, container: %d slot: %d id %d", interfaceId, slot, id));
        }

        FifthContainerAction.fifthAction(player, interfaceId, slot, id);
        player.getInventory().refresh();
    }
}
