package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.player.Player;

public class SetupChannelInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.CLAN_SETUP;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().ifOpenSub(GameInterface.CLAN_SETUP.getId(), 40, PaneType.FIXED, true);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
