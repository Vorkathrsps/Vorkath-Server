package com.cryptic.clientscripts.impl.equipment.guideprice;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class GuidePriceInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.GUIDE_PRICE;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(2, 0, 28));
        player.getPacketSender().runClientScriptNew(ScriptID.TOTAL_GUIDE_PRICE_AMOUNT, 1, 1, 15, ComponentID.TOTAL_GUIDE_PRICE);
        player.getPacketSender().setComponentText(ComponentID.TOTAL_GUIDE_PRICE, "Total guide price:<br><col=ffffff>0</col>");
        player.getPacketSender().setItemMessage(ComponentID.GUIDE_PRICE_GRAND_EXCHANGE_ITEM, 6512, -1);
        GameInterface.GUIDE_PRICE_INVENTORY.open(player);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        final Item itemAtSlot = player.getPriceChecker().get(slot);
        switch (button) {
            case ComponentID.DEPOSIT_ALL_GUIDE_PRICE -> {
                if (player.getInventory().isEmpty()) return;
                player.getPriceChecker().depositAll();
            }
            case ComponentID.GUIDE_PRICES_SEARCH -> {
                player.putAttrib(AttributeKey.ACTIVE_ONRESUME_INTERFACE, gameInterface().getId());
                player.getPacketSender().runClientScriptNew(ScriptID.GUIDE_PRICE_SEARCH, "Select an item to ask about its price:", 1, -1, 0);
            }
            case ComponentID.GUIDE_PRICES_ITEM_CONTAINER -> {
                switch (option) {
                    case 1 -> {
                        if (itemAtSlot == null) return;
                        player.getPriceChecker().removeItemFromSlot(itemId, 1);
                    }
                    case 2 -> {
                        if (itemAtSlot == null) return;
                        player.getPriceChecker().removeItemFromSlot(itemId, 5);
                    }
                    case 3 -> {
                        if (itemAtSlot == null) return;
                        player.getPriceChecker().removeItemFromSlot(itemId, 10);
                    }
                    case 4 -> {
                        if (itemAtSlot == null) return;
                        if (itemAtSlot.stackable()) {
                            player.getPriceChecker().removeItemFromSlot(itemId, itemAtSlot.getAmount());
                            return;
                        }
                        player.getPriceChecker().removeAllFromSlot(itemId);
                    }
                    case 5 -> {
                        if (itemAtSlot == null) return;
                        player.<Integer>setResumeAmountScript("Enter Amount:", (amount) -> {
                            if (slot == -1) return true;
                            player.getPriceChecker().removeItemFromSlot(itemId, amount);
                            return true;
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onResumePObj(Player player, int id) {
        final Item item = Item.of(id);
        player.getPacketSender().setItemMessage(ComponentID.GUIDE_PRICE_GRAND_EXCHANGE_ITEM, id, 1);
        player.getPacketSender().runClientScriptNew(ScriptID.TOTAL_GUIDE_PRICE_AMOUNT, 0, 1, 15, ComponentID.TOTAL_GUIDE_PRICE);
        player.getPacketSender().setComponentText(ComponentID.TOTAL_GUIDE_PRICE, (item.name() + ":<br><col=ffffff>" + Utils.formatValueCommas(item.getValue()) + "coins</col>"));
        player.clearAttrib(AttributeKey.ACTIVE_ONRESUME_INTERFACE);
    }
}
