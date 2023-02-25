package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextClickPacketListener implements PacketListener {

    private static final Logger logger = LogManager.getLogger(TextClickPacketListener.class);

    @Override
    public void handleMessage(Player player, Packet packet) {
        int textFrame = packet.readInt();
        int action = packet.readByte();

        if (player == null || player.dead()) {
            return;
        }
        player.afkTimer.reset();

        player.debugMessage(String.format("frame=%d action=%d", textFrame, action));

        if(player.getPresetManager().handleButton(textFrame, action)) {
            return;
        }

        if (player.getTeleportInterface().handleButton(textFrame, action)) {
            return;
        }

        if (player.getBank().buttonAction(textFrame)) {
            return;
        }

        if (PacketInteractionManager.checkButtonInteraction(player, textFrame)) {
            return;
        }

        return;
    }
}
