package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class WindowSettingsPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int mode = packet.readByte();
        int width = packet.readShort();
        int height = packet.readShort();

        System.out.println("Window Setting: Mode(" + mode + " width=" + width + " height="  + height);
    }
}
