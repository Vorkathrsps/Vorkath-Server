package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.IncomingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ynneh
 */
public class InputListener implements PacketListener {

    private static final Logger log = LoggerFactory.getLogger(InputListener.class);

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player == null || player.dead())
            return;

        player.afkTimer.reset();

        int opcode = packet.getOpcode();

        InputScript inputScript = player.getInputScript();

        if(inputScript == null)
            return;

        var dialogueStateId = -1;

        Object value = opcode == IncomingHandler.ENTER_AMOUNT_OPCODE ? packet.readInt() : packet.readString();

        if (opcode == IncomingHandler.ENTER_AMOUNT_OPCODE)
            dialogueStateId = packet.readByte();

        if(inputScript.handle(value))
            player.finishInputScript();
    }
}
