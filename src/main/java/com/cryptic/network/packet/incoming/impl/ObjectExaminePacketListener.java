package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ObjectExaminePacketListener implements PacketListener {
    @Override
    public void handleMessage(Player player, Packet packet) throws Exception {
        int id = packet.readUnsignedShort();
        player.message(World.getWorld().examineRepository().object(id));
    }
}
