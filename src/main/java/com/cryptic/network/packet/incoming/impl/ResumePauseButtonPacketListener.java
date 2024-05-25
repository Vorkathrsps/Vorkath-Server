package com.cryptic.network.packet.incoming.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import javassist.compiler.ast.Symbol;

public class ResumePauseButtonPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        CombinedComponent combinedComponent = new CombinedComponent(packet.readInt());
        int interfaceID = combinedComponent.getInterfaceId();

        int slot = packet.readShort();

        if (interfaceID == InterfaceID.DIALOG_OPTION || interfaceID == InterfaceID.DESTROY_ITEM) {
            System.out.println("sfsdfd");
            player.getDialogueManager().select(slot);
        } else {
            if (player.getDialogueManager().isActive()) {
                player.getDialogueManager().next();
            }
        }

    }
}
