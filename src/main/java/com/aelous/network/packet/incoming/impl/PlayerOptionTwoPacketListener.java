package com.aelous.network.packet.incoming.impl;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class PlayerOptionTwoPacketListener implements PacketListener {//not used

    @Override
    public void handleMessage(Player player, Packet packet) {
        player.stopActions(false);
        int index = packet.readShort() & 0xFFFF;
        if (index > World.getWorld().getPlayers().capacity())
            return;

        System.err.println("attackin plr????");
        Player other = World.getWorld().getPlayers().get(index);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            player.debugMessage(String.format("Click 2 pid=%d", other.getIndex()));

            if (player.locked() || player.dead() || other.dead()) {
                return;
            }
            player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(other));
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);
            player.setEntityInteraction(null);
            if (player.getMovementQueue().isFollowing(other)) {
                player.getMovementQueue().resetFollowing();
                player.setEntityInteraction(null);
            }
        }
    }
}
