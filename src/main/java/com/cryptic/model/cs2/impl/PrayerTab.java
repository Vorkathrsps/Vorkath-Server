package com.cryptic.model.cs2.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

public class PrayerTab extends InterfaceBuilder {

    private static final int SHOW_LOWER_TIERS = 6574;
    private static final int SHOW_MULTI = 6574;
    private static final int RAPID_HEALING = 6576;
    private static final int LACK_LEVEL = 6577;
    private static final int LACK_REQ = 541;

    @Override
    public GameInterface gameInterface() {
        return  GameInterface.PRAYER_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(WidgetUtil.componentToId(ComponentID.PRAYER_FILTERING), 0, 4));
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.PRAYER_FILTERING){
            int varbit = switch (slot) {
                case 0 -> SHOW_LOWER_TIERS;
                case 1 -> SHOW_MULTI;
                case 2 -> RAPID_HEALING;
                case 3 -> LACK_LEVEL;
                case 4 -> LACK_REQ;
                default -> 0;
            };

            player.varps().toggleVarbit(varbit);
        }
    }
}
