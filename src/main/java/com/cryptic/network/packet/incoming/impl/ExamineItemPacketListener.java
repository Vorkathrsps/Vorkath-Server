package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ExamineItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int item = packet.readShort();
        int interfaceId = packet.readInt();

        if (player == null || player.dead()) {
            return;
        }

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }
        ItemDefinition definition = ItemDefinition.cached.get(item);
        if (definition != null) {
            player.message(definition.description);
        }
    }

}
