package com.cryptic.model.cs2.impl.inventory;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.content.packet_actions.interactions.items.*;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.cs2.interfaces.InterfaceHandler;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.inventory.Inventory;

public class InventoryInterface extends InterfaceBuilder {
    public InventoryInterface() {
//        setEvents(new EventNode(0, 0, 27));
    }

    @Override
    protected GameInterface gameInterface() {
        return GameInterface.INVENTORY_TAB;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.INVENTORY_CONTAINER) {
            if (player.locked() || player.dead()) {
                return;
            }

            if (player.busy()) {
                return;
            }

            if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.getBankPin().openIfNot();
                return;
            }

            if(player.askForAccountPin()) {
                player.sendAccountPinMessage();
                return;
            }

            player.afkTimer.reset();

            Inventory inventory = player.getInventory();
            if (slot < 0 || slot >= inventory.capacity()) {
                return;
            }

            InterfaceHandler.closeModals(player);

            Item item = inventory.get(slot);
            if (item == null || item.getId() != itemId) {
                inventory.refresh();
                return;
            }

            if (option == 10) {
                ItemDefinition def = ItemDefinition.cached.get(itemId);
                player.message(def.description);
                return;
            }

            player.stopActions(false);
            player.putAttrib(AttributeKey.ITEM_SLOT, slot);
            player.putAttrib(AttributeKey.FROM_ITEM, item);
            player.putAttrib(AttributeKey.ITEM_ID, item.getId());
            switch (option) {
                case 2:
                    ItemActionOne.click(player, item);
                    break;
                case 3:
                    ItemActionTwo.click(player, item);
                    break;
                case 4:
                    ItemActionThree.click(player, item);
                    break;
                case 6:
                    ItemActionFour.click(player, item);
                    break;
                case 7:
                    ItemActionFive.click(player, item);
                    break;
            }
        }
    }
}
