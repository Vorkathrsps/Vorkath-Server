package com.cryptic.clientscripts.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class LogoutTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.LOGOUT_TAB;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.LOGOUT_PANEL_LOGOUT_BUTTON) {
            if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_FORFEIT.ordinal()]) {
                player.message("You cannot log out at the moment.");
                return;
            }
            player.putAttrib(AttributeKey.LOGOUT_CLICKED, true);
        } else if (button == ComponentID.LOGOUT_PANEL_WORLD_SWITCHER_BUTTON) {
            player.getPacketSender().sendMessage("Coming Soon");
        }
    }
}
