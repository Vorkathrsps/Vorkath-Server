package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionChangePacketListener implements PacketListener {
    private static final Logger logger = LogManager.getLogger(RegionChangePacketListener.class);
    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.isAllowRegionChangePacket()) {
            try {
                RegionManager.loadMapFiles(player.tile().getX(), player.tile().getY());
                player.getPacketSender().deleteRegionalSpawns();
                GroundItemHandler.updateRegionItems(player);
                ObjectManager.onRegionChange(player);
                player.getFarming().regionChanged();
                PacketInteractionManager.onRegionChange(player);
                player.setAllowRegionChangePacket(false);
                player.afkTimer.reset();
            } catch (Exception e) {
                logger.error(player.toString() + " has encountered an error loading region " + player.tile().region() + " regionid " + player.tile().region());
                logger.error("sadge", e);
            }
        }
    }
}
