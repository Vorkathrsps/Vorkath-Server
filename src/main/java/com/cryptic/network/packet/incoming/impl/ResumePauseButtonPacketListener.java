package com.cryptic.network.packet.incoming.impl;

import com.cryptic.clientscripts.InterfaceID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;

public class ResumePauseButtonPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        CombinedComponent combinedComponent = new CombinedComponent(packet.readInt());
        int interfaceID = combinedComponent.getInterfaceId();

        int slot = packet.readShort();

        if (interfaceID == InterfaceID.DIALOG_OPTION || interfaceID == InterfaceID.DESTROY_ITEM) {
            player.getDialogueManager().select(slot);
        } else {
            if (player.getDialogueManager().isActive()) {
                player.getDialogueManager().next();
            }
        }

    }
}
