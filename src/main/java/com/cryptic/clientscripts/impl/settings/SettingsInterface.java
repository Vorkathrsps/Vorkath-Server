package com.cryptic.clientscripts.impl.settings;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

public class SettingsInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.SETTINGS_INTERFACE;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().runClientScriptNew(5474, 51121585, 1342, 10, 2, 85, 16765184, 0);
        setEvents(new EventNode(21, 0, 168));
        setEvents(new EventNode(23, 0, 8));
        setEvents(new EventNode(19, 0, 413));
        setEvents(new EventNode(28, 0, 41));
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.ALL_SETTINGS_CLOSE) {
            this.gameInterface().close(player);
        }
    }
}
