package com.aelous.network.packet.incoming.impl;

import com.aelous.model.content.skill.impl.firemaking.LogLighting;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.RouteFinder;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.util.Optional;

/**
 * @author PVE
 * @Since augustus 24, 2020
 */
public class ItemOnGroundItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int interfaceId = packet.readLEShort();
        int itemUsedId = packet.readShortA();
        int gItem = packet.readShort();
        int y = packet.readShortA();
        int slot = packet.readLEShortA();
        int x = packet.readShort();

        player.debugMessage(String.format("itemOnGroundItem, interface: %d slot: %d itemUsedId: %d gItem: %d x: %d y: %d", interfaceId, slot, itemUsedId, gItem, x, y));

        Optional<GroundItem> groundItem = GroundItemHandler.getGroundItem(gItem, new Tile(x, y), player);

        if (!player.locked() || !player.dead()) {
            Item item = player.inventory().get(slot);
            if (item == null)
                return;

            player.stopActions(true);
            player.putAttrib(AttributeKey.FROM_ITEM, item);
            player.putAttrib(AttributeKey.ITEM_SLOT, slot);
            player.putAttrib(AttributeKey.INTERACTED_GROUNDITEM, groundItem.get());
            player.putAttrib(AttributeKey.INTERACTION_OPTION, -1);

            player.getRouteFinder().routeGroundItem(groundItem.get(), distance -> {
                    int diffX = x - groundItem.get().getTile().getX();
                    int diffY = y - groundItem.get().getTile().getY();
                    boolean mask = (RouteFinder.getDirectionMask(diffX, diffY)  & 0x1) != 0;
                    int faceCoordX = x * 2 + (mask ? diffY : diffX);
                    int faceCoordY = y * 2 + (mask ? diffX : diffY);
                    Tile position = new Tile(faceCoordX, faceCoordY);
                    player.getCombat().reset();
                    player.setPositionToFace(position);
                LogLighting.onInvitemOnGrounditem(player, item);
            });
        }
    }
}
