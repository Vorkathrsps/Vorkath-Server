package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ResumePNamedDialogPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        String stringDialog = packet.readString();

        System.out.println(STR."ResumePNamedDialogPacketListener: \{stringDialog}");

    }
}
