package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.IncomingHandler;

/**
 * Represents a packet used for handling dialogues.
 * This specific packet currently handles the action
 * for clicking the "next" option during a dialogue_old.
 * 
 * @author Professor Oak
 */

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
