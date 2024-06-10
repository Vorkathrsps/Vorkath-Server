package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorldMapClickPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int var1 = packet.readShort();
        int var2 = packet.readShort();
        int var3 = packet.readInt();
        int var4 = packet.readByte();
    }
}
