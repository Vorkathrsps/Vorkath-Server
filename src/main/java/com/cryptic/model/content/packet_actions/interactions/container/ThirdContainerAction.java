package com.cryptic.model.content.packet_actions.interactions.container;

import com.cryptic.model.content.duel.Dueling;
import com.cryptic.model.content.skill.impl.crafting.impl.Jewellery;
import com.cryptic.model.content.skill.impl.smithing.EquipmentMaking;
import com.cryptic.model.items.trade.Trading;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerStatus;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.container.shop.ShopUtility;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

import static com.cryptic.model.content.skill.impl.smithing.EquipmentMaking.*;
import static com.cryptic.model.inter.InterfaceConstants.*;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class ThirdContainerAction {

    public static void thirdAction(Player player, int interfaceId, int slot, int id) {
        if (PacketInteractionManager.checkItemContainerActionInteraction(player, new Item(id), slot, interfaceId, 3)) {
            return;
        }

        if (player.getRunePouch().removeFromPouch(interfaceId, id, slot, 3)) {
            return;
        }

        if (TradingPost.handleSellingItem(player, interfaceId, id, 10)) {
            return;
        }

        if (player.getRunePouch().moveToRunePouch(interfaceId, id, slot, 3)) {
            return;
        }

        if (interfaceId == EQUIPMENT_CREATION_COLUMN_1 || interfaceId == EQUIPMENT_CREATION_COLUMN_2 || interfaceId == EQUIPMENT_CREATION_COLUMN_3 || interfaceId == EQUIPMENT_CREATION_COLUMN_4 || interfaceId == EQUIPMENT_CREATION_COLUMN_5) {
            if (player.getInterfaceManager().isInterfaceOpen(EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID)) {
                EquipmentMaking.initialize(player, id, interfaceId, slot, 10);

            }
        }

        if (interfaceId == 4233 || interfaceId == 4239 || interfaceId == 4245) {
            Jewellery.click(player, id, 10);
            return;
        }

        /* Looting bag */
        if (interfaceId == LOOTING_BAG_BANK_CONTAINER_ID) {
            Item item = player.getLootingBag().get(slot);
            if (item == null) {
                return;
            }
            boolean banking = player.getAttribOr(AttributeKey.BANKING, false);

            if (banking) {
                player.getLootingBag().withdrawBank(item.createWithAmount(10), slot);
                return;
            }
        }

        if (interfaceId == LOOTING_BAG_DEPOSIT_CONTAINER_ID) {
            Item item = player.inventory().get(slot);
            if (item == null) {
                return;
            }

            player.getLootingBag().deposit(item, 10, null);
        }

        if (interfaceId == WITHDRAW_BANK) {
            player.getBank().withdraw(id, slot, 10);
        }

        if (interfaceId == INVENTORY_STORE) {
            boolean banking = player.getAttribOr(AttributeKey.BANKING, false);
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().deposit(slot, 10);
                return;
            }

            if (banking) {
                player.getBank().deposit(slot, 10);
                return;
            }
        }

        if (interfaceId == PRICE_CHECKER_DISPLAY_ID) {
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().withdraw(id, 10);
                return;
            }
        }

        if (interfaceId == ShopUtility.ITEM_CHILD_ID || interfaceId == ShopUtility.SLAYER_BUY_ITEM_CHILD_ID) {
            Shop.exchange(player, id, slot, 3, true);
            return;
        }

        if (interfaceId == SHOP_INVENTORY) {
            Shop.exchange(player, id, slot, 3, false);
            return;
        }

        // Withdrawing items from duel
        if (interfaceId == Dueling.MAIN_INTERFACE_CONTAINER) {
            if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 10, slot, player.getDueling().getContainer(), player.inventory());
                return;
            }
        }

        if (interfaceId == REMOVE_INVENTORY_ITEM) { // Duel/Trade inventory
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 10, slot, player.inventory(), player.getTrading().getContainer());
                return;
            } else if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 10, slot, player.inventory(), player.getDueling().getContainer());
                return;
            }
        }

        if (interfaceId == Trading.CONTAINER_INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 10, slot, player.getTrading().getContainer(), player.inventory());
                return;
            }
        }

        if (interfaceId == 48542) {
            player.getPriceChecker().withdraw(id, 10);
            return;
        }
    }
}
