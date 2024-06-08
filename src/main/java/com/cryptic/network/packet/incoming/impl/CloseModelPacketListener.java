package com.cryptic.network.packet.incoming.impl;

import com.cryptic.clientscripts.interfaces.InterfaceHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class CloseModelPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        InterfaceHandler.closeModals(player);
    }
}
