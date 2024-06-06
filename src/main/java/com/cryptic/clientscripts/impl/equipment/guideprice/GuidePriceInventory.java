package com.cryptic.clientscripts.impl.equipment.guideprice;

import com.cryptic.clientscripts.InterfaceID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

public class GuidePriceInventory extends InterfaceBuilder {
    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(0, 0, 27));
        player.getPacketSender().runClientScriptNew(149, 15597568, 93, 4, 7, 0, -1, "Add<col=ff9040>", "Add-5<col=ff9040>", "Add-10<col=ff9040>", "Add-All<col=ff9040>", "Add-X<col=ff9040>");
    }

    @Override
    public GameInterface gameInterface() {
        return GameInterface.GUIDE_PRICE_INVENTORY;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        int interfaceID = WidgetUtil.componentToInterface(button);
        int child = WidgetUtil.componentToId(button);
        if (interfaceID == InterfaceID.GUIDE_PRICES_INVENTORY && child == 0) {
            player.getEquipment().equip(slot);
        }
    }

}
