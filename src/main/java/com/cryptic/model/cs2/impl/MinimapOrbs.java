package com.cryptic.model.cs2.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class MinimapOrbs extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.MINIMAP_ORBS;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.MINIGAMES_SPEC) {
            player.toggleSpecialAttack();
        }
    }
}
