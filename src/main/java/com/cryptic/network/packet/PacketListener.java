package com.cryptic.network.packet;

import com.cryptic.model.entity.player.Player;


/**
 * Represents a Packet received from client.
 * 
 * @author Gabriel Hannason
 */

public interface PacketListener {
    /**
     * Executes the packet.
     * @param player    The player to which execute the packet for.
     * @param packet    The packet being executed.
     */
    void handleMessage(Player player, Packet packet) throws Exception;
}
