package com.aelous.network.packet.incoming.impl;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class PlayerOptionThreePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        player.stopActions(false);
        int index = packet.readLEShortA() & 0xFFFF;
        if (index > World.getWorld().getPlayers().capacity())
            return;

        Player other = World.getWorld().getPlayers().get(index);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            player.debugMessage(String.format("Click 3 pid=%d", other.getIndex()));

            // Make sure it's not us.
            if (other.getIndex() == player.getIndex() || other.getUsername().equalsIgnoreCase(player.getUsername())) {
                return;
            }

            if (!player.locked() && !player.dead()) {
                player.setPositionToFace(other.tile());

                if (!other.dead()) {
                    player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
                    player.putAttrib(AttributeKey.INTERACTION_OPTION, 3);
                    player.setEntityInteraction(other);

                    TargetRoute.set(player, other, () -> {
                        player.runFn(1, () -> {
                            player.setPositionToFace(other.tile());
                            player.setEntityInteraction(null);
                        });
                        if (player.getMovementQueue().isFollowing(other)) {
                            player.getMovementQueue().resetFollowing();
                            player.setEntityInteraction(null);
                        }
                        if (player.getController() != null) {
                            player.getController().onPlayerRightClick(player, other, 3);
                        }
                    });
                }
            }
        }
    }
}
