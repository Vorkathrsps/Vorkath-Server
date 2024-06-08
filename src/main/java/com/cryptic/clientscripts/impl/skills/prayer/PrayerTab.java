package com.cryptic.clientscripts.impl.skills.prayer;

import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.combat.prayer.Prayer;
import com.cryptic.model.entity.combat.prayer.PrayerManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.WidgetUtil;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
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

            if (player.skills().level(Skills.PRAYER) <= 0 && player.skills().xpLevel(Skills.PRAYER) >= prayer.getLevel()) {
                player.getPacketSender().runClientScriptNew(ScriptID.OUT_OF_PRAYER, 112);
                return;
            }

            if (player.skills().xpLevel(Skills.PRAYER) < prayer.getLevel()) {
                player.message("You do not meet the requirements to activate this Prayer.");
                return;
            }

            player.getPrayer().activate(prayer);
        }
    }

}
