package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.packet_actions.interactions.container.ModifiableXAction;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class ModifiableXPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int slot = packet.readShortA();
        final int interfaceId = packet.readShort();
        final int id = packet.readShortA();
        final int amount = packet.readInt();

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

        player.debugMessage(String.format("modifiableX action, slot: %d interfaceId: %d id %d amount %d", slot, interfaceId, id, amount));

        ModifiableXAction.modifiableXAction(player, slot, interfaceId, id, amount);
    }
}
