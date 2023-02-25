package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.skill.impl.firemaking.LogLighting;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.util.Optional;

/**
 * This packet is received when a player
 * clicks on the second option on a ground item.
 * An example being the "light" option on logs that 
 * are on the ground.
 * 
 * @author Professor Oak
 */
public class SecondGroundItemOptionPacketListener implements PacketListener {

    @Override
    public void handleMessage(final Player player, Packet packet) {
        final int y = packet.readLEShort();
        final int itemId = packet.readShort();
        final int x = packet.readLEShort();

        final Tile tile = new Tile(x, y, player.tile().getLevel());

        if (player.dead()) {
            return;
        }

        if (player.busy())
            return;

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }
        //Get ground item..
        Optional<GroundItem> item = GroundItemHandler.getGroundItem(itemId, tile, player);

        if (!player.locked() || !player.dead()) {
            player.stopActions(false);
            player.afkTimer.reset();
            player.putAttrib(AttributeKey.INTERACTED_GROUNDITEM, item.get());
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);

            player.getRouteFinder().routeGroundItem(item.get(), distance -> {
                //Make sure distance isn't way off..
                player.getCombat().reset();

                if (Math.abs(player.tile().getX() - x) > 25 || Math.abs(player.tile().getY() - y) > 25) {
                    player.getMovementQueue().clear();
                    return;
                }

                item.ifPresent(groundItem -> LogLighting.onGroundItemOption2(player, groundItem.getItem()));
            });
        }
    }
}
