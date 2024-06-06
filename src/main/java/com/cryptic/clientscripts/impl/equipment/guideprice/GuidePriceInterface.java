package com.cryptic.clientscripts.impl.equipment.guideprice;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;

public class GuidePriceInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.GUIDE_PRICE;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(2, 0, 27));
        player.getPacketSender().runClientScriptNew(ScriptID.GUIDE_PRICE_STRINGS, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        player.getPacketSender().runClientScriptNew(600, 1, 1, 15, 30408716);
        GameInterface.GUIDE_PRICE_INVENTORY.open(player);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        switch (button) {
            case ComponentID.GUIDE_PRICES_SEARCH -> player.getPacketSender().runClientScriptNew(ScriptID.SEARCH_GUIDE_PRICE, "Select an item to ask about its price:", 1, -1, 0);
        }
    }
}
