package com.cryptic.clientscripts.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class MinimapOrbs extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.MINIMAP_ORBS;
    }

    @Override
    public void beforeOpen(Player player) {

    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.MINIMAP_WORLDMAP_OPTIONS) {
            if (option == 2) {
                boolean isOpen = player.<Boolean>getAttribOr(AttributeKey.WORLD_MAP_ACTIVE, false);
                if (isOpen) {
                    player.putAttrib(AttributeKey.WORLD_MAP_ACTIVE,false);
                    GameInterface.WORLD_MAP.close(player);
                } else {
                    player.putAttrib(AttributeKey.WORLD_MAP_FULLSCREEN,false);
                    GameInterface.WORLD_MAP.open(player);
                }
            } else if (option == 3) {
                player.putAttrib(AttributeKey.WORLD_MAP_FULLSCREEN,true);
                GameInterface.WORLD_MAP.open(player);
            }
        }
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
