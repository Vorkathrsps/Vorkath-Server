package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class PlayerOptionOnePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        player.stopActions(false);
        int index = packet.readShort() & 0xFFFF;
        if (index > World.getWorld().getPlayers().capacity())
            return;
        Player other = World.getWorld().getPlayers().get(index);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            player.debugMessage(String.format("Click 1 pid=%d", other.getIndex()));

            if (player.locked() || player.dead()) {
                return;
            }
            //Face the player that we will be interacting with.
            player.setEntityInteraction(other);
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 1);
            player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
            player.setEntityInteraction(other);

            TargetRoute.set(player, other, () -> {
                player.runFn(1, () -> {
                    player.setEntityInteraction(null);
                });
                if (player.getMovementQueue().isFollowing(other)) {
                    player.getMovementQueue().resetFollowing();
                    player.setEntityInteraction(null);
                }
                if (!player.getControllers().isEmpty()) {
                    for (Controller controller : player.getControllers()) {
                        controller.onPlayerRightClick(player, other, 1);
                    }
                }
            });
        }
    }
}
