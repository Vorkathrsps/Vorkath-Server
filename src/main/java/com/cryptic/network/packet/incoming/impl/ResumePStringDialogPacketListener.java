package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ResumePStringDialogPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        String stringDialog = packet.readString();

        System.out.println("ResumePStringDialogPacketListener: " + stringDialog);

    }
}
