package com.cryptic.clientscripts.impl.prayer;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.combat.prayer.PrayerManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

public class PrayerTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.PRAYER_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(WidgetUtil.componentToId(ComponentID.PRAYER_FILTERING), 0, 4));
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.PRAYER_FILTERING) {
            player.varps().toggleVarbit(PrayerManager.getFilterConfiguration(slot));
        }

        Prayer prayer = player.getPrayer().getPrayer(button);

        if (prayer != null) {

            if (player.getPrayer().getSkillLevel() < prayer.getLevel()) {
                player.message("You do not meet the requirements to activate this Prayer.");
                return;
            }

            if (player.getPrayer().getSkillLevel() <= 0) {
                player.message("You need to recharge your Prayer at an altar.");
                return;
            }

            player.getPrayer().activate(prayer);
        }
    }

}
