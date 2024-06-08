package com.cryptic.clientscripts.impl.social;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class IgnoreListInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.IGNORE_LIST_TAB;
    }

    @Override
    public void beforeOpen(Player player) {

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == 28311553) {
            player.interfaces.handleRelationShipTab(true);
        }
    }


}