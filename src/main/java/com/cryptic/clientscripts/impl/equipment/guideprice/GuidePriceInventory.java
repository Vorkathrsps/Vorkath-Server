package com.cryptic.clientscripts.impl.equipment.guideprice;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.InventoryID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.inventory.Inventory;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class GuidePriceInventory extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.GUIDE_PRICE_INVENTORY;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(0, 0, 28));
        player.getPacketSender().runClientScriptNew(ScriptID.ADD_AMOUNT_MENU_OPTIONS, ComponentID.GUIDE_PRICE_SLOT, InventoryID.INVENTORY, 4, 7, 0, -1, "Add<col=ff9040>", "Add-5<col=ff9040>", "Add-10<col=ff9040>", "Add-All<col=ff9040>", "Add-X<col=ff9040>");
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        final Item itemAtSlot = player.getInventory().get(slot);
        if (button == ComponentID.GUIDE_PRICE_SLOT) {
            switch (option) {
                case 1 -> {
                    if (itemAtSlot == null) return;
                    player.getPriceChecker().sendItemToSlot(slot, 1);
                }
                case 2 -> {
                    if (itemAtSlot == null) return;
                    player.getPriceChecker().sendItemToSlot(slot, 5);
                }
                case 3 -> {
                    if (itemAtSlot == null) return;
                    player.getPriceChecker().sendItemToSlot(slot, 10);
                }
                case 4 -> {
                    if (itemAtSlot == null) return;
                    if (itemAtSlot.stackable()) {
                        player.getPriceChecker().sendItemToSlot(itemId, itemAtSlot.getAmount());
                        return;
                    }
                    player.getPriceChecker().sendAllToSlot(itemId);
                }
                case 5 -> {
                    if (itemAtSlot == null) return;
                    player.<Integer>setResumeAmountScript("Enter Amount:", (amount) -> {
                        if (slot == -1) return true;
                        player.getPriceChecker().sendItemToSlot(slot, amount);
                        return true;
                    });
                }
            }
        }
    }

    @Override
    public void onModalClosed(Player player) {
        if (player.getPriceChecker().isEmpty()) return;
        player.getPriceChecker().withdrawAll();
    }
}
