package com.cryptic.network.packet.incoming.impl;

import com.cryptic.clientscripts.InterfaceID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.clientscripts.interfaces.InterfaceHandler;
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

        System.out.println("[PAUSE_PACKET]=" + "interfaceid=" + interfaceID + " Slot=" + slot);

        InterfaceBuilder builder = InterfaceHandler.find(interfaceID);
        if (builder != null) {
            builder.onResumePause(player, slot);
        }

        if (interfaceID == InterfaceID.DIALOG_OPTION || interfaceID == InterfaceID.DESTROY_ITEM) {
            player.getDialogueManager().select(slot);
        } else {
            if (player.getDialogueManager().isActive()) {
                player.getDialogueManager().next();
            }
        }

    }
}
