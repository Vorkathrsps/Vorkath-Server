package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.relations.PlayerRelations.PrivateChatStatus;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

public class ChatSettingsPacketListener implements PacketListener {

    //TODO complete this packet later

    @Override
    public void handleMessage(Player player, Packet packet) {
        @SuppressWarnings("unused")
        int publicMode = packet.readByte();

        int privateMode = packet.readByte();

        @SuppressWarnings("unused")
        int tradeMode = packet.readByte();
        
        @SuppressWarnings("unused")
        int clanChatMode = packet.readByte();

        if (player == null || player.dead()) {
            return;
        }
        
        player.afkTimer.reset();

        /*
         * Did the player change their private chat status?
         * If yes, update status for all friends.
         */
        if (privateMode > PrivateChatStatus.values().length) {
            return;
        }

        PrivateChatStatus privateChatStatus = PrivateChatStatus.values()[privateMode];
        if (player.getRelations().getStatus() != privateChatStatus) {
            player.getRelations().setStatus(privateChatStatus);
        }
    }
}
