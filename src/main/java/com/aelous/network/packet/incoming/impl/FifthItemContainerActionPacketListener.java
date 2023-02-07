package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.packet_actions.interactions.container.FifthContainerAction;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

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
        if (GameServer.properties().debugMode && player.getPlayerRights().isDeveloper(player)) {
            player.debugMessage(String.format("fifth action, container: %d slot: %d id %d", interfaceId, slot, id));
        }

        FifthContainerAction.fifthAction(player, interfaceId, slot, id);
    }
}
