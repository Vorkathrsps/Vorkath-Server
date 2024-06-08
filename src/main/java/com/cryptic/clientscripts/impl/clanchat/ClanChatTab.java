package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class ClanChatTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SIDE_CHANNELS;
    }

    @Override
    public void beforeOpen(Player player) {
        GameInterface.FRIENDS_CHANNEL.open(player);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        switch (button) {
            case ComponentID.FRIENDS_CHANNEL -> player.getPacketSender().sendSubInterface(GameInterface.FRIENDS_CHANNEL.getId(), 7, PaneType.CHAT_TAB_HEADER);
            case ComponentID.YOUR_CLAN -> player.getPacketSender().sendSubInterface(GameInterface.YOUR_CLAN.getId(), 7, PaneType.CHAT_TAB_HEADER);
            case ComponentID.GUEST_CHANNEL -> player.getPacketSender().sendSubInterface(GameInterface.CLAN_GUEST.getId(), 7, PaneType.CHAT_TAB_HEADER);
            case ComponentID.GROUPING_CHANNEL -> player.getPacketSender().sendSubInterface(GameInterface.CLAN_GROUP.getId(), 7, PaneType.CHAT_TAB_HEADER);
        }
    }
}
