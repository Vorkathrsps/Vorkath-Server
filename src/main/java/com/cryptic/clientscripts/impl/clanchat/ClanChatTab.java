package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

public class ClanChatTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SIDE_CHANNELS;
    }

    @Override
    public void beforeOpen(Player player) {

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

/*    @Override
    public void isIfOpenSub(Player player) {
        player.getPacketSender().ifOpenSub(GameInterface.FRIENDS_CHANNEL.getId(), 7, PaneType.CHAT_TAB_HEADER, true);
    }*/
}
