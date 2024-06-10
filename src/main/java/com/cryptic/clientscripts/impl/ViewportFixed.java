package com.cryptic.clientscripts.impl;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.clientscripts.constants.EventConstants;
import com.cryptic.clientscripts.impl.skills.magic.MagicTab;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

public class ViewportFixed extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.FIXED_VIEWPORT;
    }

    @Override
    public void beforeOpen(Player player) {

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
                player.varps().toggleVarbit(Varbits.SPELLBOOK_FILTERING);
            } else if (button == ComponentID.FIXED_VIEWPORT_OPTIONS_TAB) {
                GameInterface.SETTINGS.open(player);
            }
        }
    }
}
