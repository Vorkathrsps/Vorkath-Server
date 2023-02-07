package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

public class ExamineItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int item = packet.readShort();
        int interfaceId = packet.readInt();
        // 317 doesnt send item slot so we cant get the amount

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

        player.message("%s", World.getWorld().examineRepository().item(item));
    }

}
