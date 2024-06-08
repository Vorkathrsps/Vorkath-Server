package com.cryptic.network.packet.incoming.impl;

import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.clientscripts.InterfaceHandler;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ResumePObjDialogPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int value = packet.readShort();
        System.out.println("ResumePObjDialogPacketListener: " + value);

        final int interfaceId = player.<Integer>getAttribOr(AttributeKey.ACTIVE_ONRESUME_INTERFACE, -1);
        final InterfaceBuilder builder = InterfaceHandler.find(interfaceId);

        if (builder != null) builder.onResumePObj(player, value);
    }
}
