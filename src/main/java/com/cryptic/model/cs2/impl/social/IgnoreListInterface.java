package com.cryptic.model.cs2.impl.social;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class IgnoreListInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.IGNORE_LIST_TAB;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == 28311553) {
            player.interfaces.handleRelationShipTab(true);
        }
    }


}
