package com.aelous.network.packet.incoming.impl;

import com.aelous.model.content.minigames.impl.Barrows;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.region.RegionManager;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
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
