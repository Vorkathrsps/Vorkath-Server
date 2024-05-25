package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class CloseInterfacePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.getInterfaceManager().isInterfaceOpen(TradingPost.BUY_CONFIRM_UI_ID)) {
            player.getInterfaceManager().close();
            return;
        }

        if (player.dead()) {
            return;
        }
        if (CombatFactory.inCombat(player)) {
            return;
        }

        player.afkTimer.reset();

        boolean newAccount = player.getAttribOr(AttributeKey.NEW_ACCOUNT, false);
        if (newAccount) {
            //The player can select their appearance here.
            player.getInterfaceManager().open(3559);
            return;
        }

        player.getInterfaceManager().close();
        //Because the play button is calling this packet due to the Client.
        //We have to hardcode the starter here after all actions are completed.
        //This is the entry point of new players.
    }
}
