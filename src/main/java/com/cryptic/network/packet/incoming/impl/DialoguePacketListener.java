package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.IncomingHandler;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a packet used for handling dialogues.
 * This specific packet currently handles the action
 * for clicking the "next" option during a dialogue_old.
 * 
 * @author Professor Oak
 */

@Deprecated(forRemoval = true)
public class DialoguePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {

        if (player == null || player.dead()) {
            return;
        }
        player.afkTimer.reset();

        if (packet.getOpcode() == IncomingHandler.DIALOGUE_OPCODE) {
            int interfaceId = packet.readShort();
            if (player.getDialogueManager().isActive()) {
                if (player.getDialogueManager().next()) {
                    return;
                }
            }
        }
    }
}
