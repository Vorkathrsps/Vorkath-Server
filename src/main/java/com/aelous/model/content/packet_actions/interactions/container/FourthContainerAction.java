package com.aelous.model.content.packet_actions.interactions.container;

import com.aelous.model.content.duel.Dueling;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.items.trade.Trading;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.PlayerStatus;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.shop.Shop;
import com.aelous.model.items.container.shop.ShopUtility;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;

import static com.aelous.model.inter.InterfaceConstants.*;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FourthContainerAction {

    public static void fourthAction(Player player, int interfaceId, int slot, int id) {
        var count = player.inventory().count(id);
        Item item = new Item(id, count);
        if (PacketInteractionManager.checkItemContainerActionInteraction(player, item, slot, interfaceId, 4)) {
            return;
        }

        if (player.getRunePouch().removeFromPouch(interfaceId, id, slot, 4)) {
            return;
        }

        if (TradingPost.handleSellingItem(player, interfaceId, id, count))
            return;

        if (player.getRunePouch().moveToRunePouch(interfaceId, id, slot, 4)) {
            return;
        }

        /* Looting bag */
        if (interfaceId == LOOTING_BAG_BANK_CONTAINER_ID) {
            Item lootingBagItem = player.getLootingBag().get(slot);
            if (lootingBagItem == null) {
                return;
            }
            boolean banking = player.getAttribOr(AttributeKey.BANKING, false);

            if (banking) {

                player.setAmountScript("How many would you like to deposit?", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        int amount = (Integer) value;

                        if (id < 0 || slot < 0 || amount <= 0)
                            return false;

                        if (!player.getLootingBag().contains(id))
                            return false;

                        if (amount > player.getLootingBag().count(id))
                            amount = player.getLootingBag().count(id);

                        player.getLootingBag().withdrawBank(new Item(id, amount), slot);
                        return true;
                    }
                });
            }
        }

        if (interfaceId == LOOTING_BAG_DEPOSIT_CONTAINER_ID) {
            Item lootingBagItem = player.inventory().get(slot);
            if (lootingBagItem == null) {
                return;
            }

            player.setAmountScript("How many would you like to deposit?", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    int amount = (Integer) value;
                    if (id < 0 || slot < 0 || amount <= 0) {
                        return false;
                    }
                    if (!player.inventory().contains(id)) {
                        return false;
                    }
                    player.getLootingBag().deposit(new Item(id, amount), amount, null);
                    return true;
                }
            });
            return;
        }

        if (interfaceId == WITHDRAW_BANK) {
            player.getBank().withdraw(id, slot, Integer.MAX_VALUE);
        }

        if (interfaceId == INVENTORY_STORE) {
            boolean banking = player.getAttribOr(AttributeKey.BANKING, false);
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().deposit(slot, Integer.MAX_VALUE);
            }

            if (banking) {
                player.getBank().deposit(slot, Integer.MAX_VALUE);
            }
        }

        if (interfaceId == PRICE_CHECKER_DISPLAY_ID) {
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                int amount_to_withdraw = player.getPriceChecker().count(id);
                player.getPriceChecker().withdraw(id, amount_to_withdraw);
                return;
            }
        }


        if (interfaceId == ShopUtility.ITEM_CHILD_ID || interfaceId == ShopUtility.SLAYER_BUY_ITEM_CHILD_ID) {
            Shop.exchange(player, id, slot, 4, true);
        }

        if (interfaceId == SHOP_INVENTORY) {
            Shop.exchange(player, id, slot, 4, false);
        }

        // Withdrawing items from duel
        if (interfaceId == Dueling.MAIN_INTERFACE_CONTAINER) {
            if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, player.getDueling().getContainer().count(id), slot,
                    player.getDueling().getContainer(), player.inventory());
            }
        }

        if (interfaceId == REMOVE_INVENTORY_ITEM) {// Duel/Trade inventory
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().deposit(slot, player.inventory().count(id));
            } else if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, player.inventory().count(id), slot,
                    player.inventory(), player.getTrading().getContainer());
            } else if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, player.inventory().count(id), slot,
                    player.inventory(), player.getDueling().getContainer());
            }
        }

        if (interfaceId == Trading.CONTAINER_INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, player.getTrading().getContainer().count(id), slot,
                    player.getTrading().getContainer(), player.inventory());
            }
        }

        if (interfaceId == 48542) {
            player.getPriceChecker().withdraw(id, player.getPriceChecker().count(id));
        }
    }
}
