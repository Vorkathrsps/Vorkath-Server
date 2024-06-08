package com.cryptic.clientscripts.impl.equipment;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.InterfaceID;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.WidgetUtil;

public class EquipmentInventory extends InterfaceBuilder {

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(0, 0, 28));
    }

    @Override
    public GameInterface gameInterface() {
        return GameInterface.EQUIPMENT_INVENTORY;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        int interfaceID = WidgetUtil.componentToInterface(button);
        int child = WidgetUtil.componentToId(button);
        if (interfaceID == InterfaceID.EQUIPMENT_INVENTORY && child == 0) {
            player.getEquipment().equip(slot);
        }
    }
}