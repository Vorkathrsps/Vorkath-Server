package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorldMapTeleportPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int mouseCoordPacked = packet.readInt();
        if (!player.getPlayerRights().isAdministrator(player)) {
            return;
        }
        player.teleport(new Tile(mouseCoordPacked));
    }
}
