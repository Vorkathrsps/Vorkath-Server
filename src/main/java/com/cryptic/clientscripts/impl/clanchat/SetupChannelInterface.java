package com.cryptic.clientscripts.impl.clanchat;

import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class SetupChannelInterface extends InterfaceBuilder {
    @Override
    public GameInterface gameInterface() {
        return GameInterface.CLAN_SETUP;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().ifOpenSubModal(gameInterface().getId(), 40, PaneType.FIXED);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
