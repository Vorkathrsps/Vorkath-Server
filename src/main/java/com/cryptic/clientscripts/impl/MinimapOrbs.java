package com.cryptic.clientscripts.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class MinimapOrbs extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.MINIMAP_ORBS;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (option == 1) {
            switch (button) {
                case ComponentID.MINIGAMES_SPEC -> player.toggleSpecialAttack();
                case ComponentID.MINIMAP_RUN_ORB -> {
                    boolean running = player.getAttribOr(AttributeKey.IS_RUNNING, false);
                    player.putAttrib(AttributeKey.IS_RUNNING, !running);
                    player.getPacketSender().sendRunStatus();
                }
            }
        }
        if (option == 2) {
            switch (button) {
                case ComponentID.MINIMAP_QUICK_PRAYER_ORB -> GameInterface.QUICK_PRAYERS.open(player);
            }
        }
    }
}
