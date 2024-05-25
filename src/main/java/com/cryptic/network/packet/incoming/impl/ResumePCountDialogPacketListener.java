package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ResumePCountDialogPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int count = packet.readInt();

        System.out.println(STR."ResumePCountDialogPacketListener: \{count}");

    }
}
