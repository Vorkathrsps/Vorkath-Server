package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

public class YourChannelInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.YOUR_CLAN;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }

    @Override
    public void beforeOpen(Player player) {

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
