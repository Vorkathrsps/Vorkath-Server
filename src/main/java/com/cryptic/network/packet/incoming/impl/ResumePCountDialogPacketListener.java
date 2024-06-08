package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.InputScript;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResumePCountDialogPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int count = packet.readInt();

        InputScript<Object> inputScript = player.getInputScript();

        if (inputScript == null) return;
        if (inputScript.handle(count)) {
            player.finishInputScript();
        }
    }
}
