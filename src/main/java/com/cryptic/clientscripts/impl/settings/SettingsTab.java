package com.cryptic.clientscripts.impl.settings;

import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

public class SettingsTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SETTINGS;
    }

    @Override
    public void beforeOpen(Player player) {

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
