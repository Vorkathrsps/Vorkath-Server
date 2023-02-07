package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

public class WidgetSlot implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int slot = packet.readUnsignedByte();

        if (player.getTrading().getInteract() != null) {
            player.getTrading().getInteract().getPacketSender().sendModified(slot);
        }
    }
}
