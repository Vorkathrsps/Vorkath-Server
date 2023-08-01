package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class WidgetSlot implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int slot = packet.readUnsignedByte();

        if (player.getTrading().getInteract() != null) {
            player.getTrading().getInteract().getPacketSender().sendModified(slot);
        }
    }
}
