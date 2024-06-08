package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class FriendsChannelInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.FRIENDS_CHANNEL;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().ifOpenSubWalkable(GameInterface.FRIENDS_CHANNEL.getId(), 7, PaneType.CHAT_TAB_HEADER);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        switch (button) {
            case ComponentID.FRIENDS_CHANNEL_JOIN -> {
            }
            case ComponentID.SETUP_CHANNEL -> GameInterface.CLAN_SETUP.open(player);
        }
    }
}
