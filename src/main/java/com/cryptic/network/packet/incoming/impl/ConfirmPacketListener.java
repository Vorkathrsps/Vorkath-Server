package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ConfirmPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int state = packet.readByte();
        int value = packet.readInt();
        switch (state) {
            case 1:
                if (value == 195) {
                    player.getPacketSender().confirm(1, 196);
                }
                break;
        case 2:
            if (value == 0) {
                player.getPacketSender().confirm(2, 0);
            }
            break;
        case 4:
            if (value == 1) {
                player.getPacketSender().confirm(4, 1);
            }
            break;
        }
    }
}
