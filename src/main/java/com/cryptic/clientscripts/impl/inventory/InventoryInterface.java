package com.cryptic.clientscripts.impl.inventory;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.content.packet_actions.interactions.items.*;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventConstants;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.clientscripts.interfaces.InterfaceHandler;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.inventory.Inventory;
import kotlin.ranges.IntRange;

import java.util.List;

public class InventoryInterface extends InterfaceBuilder {

    @Override
    protected GameInterface gameInterface() {
        return GameInterface.INVENTORY_TAB;
    }

    @Override
    public void beforeOpen(Player player) {
        player.getPacketSender().setInterfaceEvents(149, 0, new IntRange(0, 27),
            List.of(
                EventConstants.ClickOp2,
                EventConstants.ClickOp3,
                EventConstants.ClickOp4,
                EventConstants.ClickOp6,
                EventConstants.ClickOp7,
                EventConstants.ClickOp10,
                EventConstants.UseOnGroundItem,
                EventConstants.UseOnNpc,
                EventConstants.UseOnObject,
                EventConstants.UseOnPlayer,
                EventConstants.UseOnInventory,
                EventConstants.UseOnComponent,
                EventConstants.DRAG_DEPTH1,
                EventConstants.DragTargetable,
                EventConstants.ComponentTargetable
            )
        );
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
                ItemDefinition def = ItemDefinition.getInstance(itemId);
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

    @Override
    public void onTargetButton(Player player, int selectedButton, int selectedSlot, int selectedItemId, int targetButton, int targetSlot, int targetItemId) {
        if (selectedButton != ComponentID.INVENTORY_CONTAINER || targetButton != ComponentID.INVENTORY_CONTAINER) {
            return;
        }

        if (player.locked() || player.dead()) {
            return;
        }

        Inventory inventory = player.getInventory();
        if (selectedSlot < 0 || selectedSlot >= inventory.capacity() || targetSlot < 0 || targetSlot >= inventory.capacity()) {
            return;
        }

        if (selectedSlot == targetSlot) {
            return;
        }

        Item selected = inventory.get(selectedSlot);
        Item targeted = inventory.get(targetSlot);
        if (selected == null || targeted == null || selected.getId() != selectedItemId || targeted.getId() != targetItemId) {
            inventory.refresh();
            return;
        }

        player.stopActions(false);
        player.putAttrib(AttributeKey.ITEM_SLOT, selectedSlot);
        player.putAttrib(AttributeKey.ALT_ITEM_SLOT, targetSlot);
        player.putAttrib(AttributeKey.FROM_ITEM, selected);
        player.putAttrib(AttributeKey.TO_ITEM, targeted);
        player.putAttrib(AttributeKey.ITEM_ID, selected.getId());
        player.putAttrib(AttributeKey.ALT_ITEM_ID, targeted.getId());

        player.afkTimer.reset();

        // Block packet when the bank pin hasn't been entered yet
        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        ItemOnItem.itemOnItem(player, selected, targeted);
    }

    @Override
    public void onDrag(Player player, int fromButton, int fromSlot, int fromItemId, int toButton, int toSlot, int toItemId) {
        Inventory inventory = player.getInventory();
        if (fromSlot < 0 || fromSlot >= inventory.capacity() || toSlot < 0 || toSlot >= inventory.capacity()) {
            return;
        }

        inventory.swap(fromSlot, toSlot, true);
    }
}
