package com.cryptic.clientscripts.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class MagicTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return  GameInterface.SPELLBOOK_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        player.varps().setVarp(4070,1);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
