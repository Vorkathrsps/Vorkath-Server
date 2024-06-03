package com.cryptic.clientscripts.impl.social;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class FriendListInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.FRIEND_LIST_TAB;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == 28114945) {
            player.interfaces.handleRelationShipTab(true);
        }
    }

}
