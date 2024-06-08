package com.cryptic.clientscripts.impl.skills.prayer;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.interfaces.EventConstants;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class QuickPrayerInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.QUICK_PRAYERS;
    }

    @Override
    public void beforeOpen(Player player) {
        EventNode quickPrayerEvent = new EventNode(WidgetUtil.componentToId(ComponentID.QUICK_PRAYER_CONTAINER), 0, 28);
        quickPrayerEvent.getEvents().add(EventConstants.ClickOp1);
        setEvents(List.of(quickPrayerEvent));
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (slot != -1) player.getPrayer().setQuickPrayer(slot);
        if (button == ComponentID.QUICK_PRAYER_DONE) {
            GameInterface.QUICK_PRAYERS.close(player);
            GameInterface.PRAYER_TAB.open(player);
        }
    }
}
