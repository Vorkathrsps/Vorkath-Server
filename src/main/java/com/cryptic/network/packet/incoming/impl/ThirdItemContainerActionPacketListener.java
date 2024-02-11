package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.packet_actions.interactions.container.ThirdContainerAction;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.Color;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class ThirdItemContainerActionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int interfaceId = packet.readInt();
        int id = packet.readUnsignedShortA();
        int slot = packet.readUnsignedShortA();

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

        player.debugMessage(String.format("third action, container: %d slot: %d id %d", interfaceId, slot, id));

        ThirdContainerAction.thirdAction(player, interfaceId, slot, id);
        player.getInventory().refresh();
    }
}
