package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

public class CloseInterfacePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.dead()) {
            return;
        }
        if (CombatFactory.inCombat(player)) {
            return;
        }
        player.afkTimer.reset();
        player.getInterfaceManager().close();
        //Because the play button is calling this packet due to the Client.
        //We have to hardcode the starter here after all actions are completed.
        //This is the entry point of new players.

        boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);
        if (newAccount) {
            //The player can select their appearance here.
            player.getInterfaceManager().open(3559);
        }
    }
}
