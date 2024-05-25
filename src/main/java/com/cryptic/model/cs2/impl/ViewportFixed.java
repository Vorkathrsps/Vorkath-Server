package com.cryptic.model.cs2.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class ViewportFixed extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.FIXED_VIEWPORT;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (option == 2) {
            if (button == ComponentID.FIXED_VIEWPORT_PRAYER_TAB) {
                player.varps().toggleVarbit(6579);
            } else if (button == ComponentID.FIXED_VIEWPORT_MAGIC_TAB) {
                player.varps().toggleVarp(Varbits.SPELLBOOK_FILTERING);
            }
        }
    }
}
