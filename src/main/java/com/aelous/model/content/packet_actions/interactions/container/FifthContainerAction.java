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
import static com.aelous.model.entity.attributes.AttributeKey.USING_TRADING_POST;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FifthContainerAction {

    public static void fifthAction(Player player, int interfaceId, int slot, int id) {
        boolean banking = player.getAttribOr(AttributeKey.BANKING, false);
        boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
        if (PacketInteractionManager.checkItemContainerActionInteraction(player, new Item(id), slot, interfaceId, 5)) {
            return;
        }

        if (player.getRunePouch().removeFromPouch(interfaceId, id, slot, 5)) {
            return;
        }

        if (player.getRunePouch().moveToRunePouch(interfaceId, id, slot, 5)) {
            return;
        }

        /* Bank x */
        if (interfaceId == WITHDRAW_BANK) {
            if (banking) {

                player.setAmountScript("How many would you like to withdraw?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int amount = (Integer) value;
                        if (id < 0 || slot < 0 || amount <= 0)
                            return false;

                        if (player.getBank().quantityX) {
                            player.getBank().currentQuantityX = amount;
                        }
                        player.getBank().withdraw(id, slot, amount);
                        return true;
                    }
                });
            }
            return;
        }

        if (interfaceId == INVENTORY_STORE) {
            if (priceChecking) {
                player.setAmountScript("How many would you like to deposit?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int amount = (Integer) value;
                        if (id < 0 || slot < 0 || amount <= 0) {
                            return false;
                        }
                        player.getPriceChecker().deposit(slot, amount);
                        return true;
                    }
                });
            } else if (banking) {

                player.setAmountScript("How many would you like to deposit?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int input = (Integer) value;
                        if (id < 0 || slot < 0 || input <= 0) {
                            return false;
                        }
                        player.getBank().deposit(slot, input);
                        return true;
                    }
                });
            }
        }

        if (interfaceId == PRICE_CHECKER_DISPLAY_ID) {
            player.setAmountScript("How many would you like to withdraw?", new InputScript() {
                @Override
                public boolean handle(Object value) {
                    int amount = (Integer) value;
                    if (id < 0 || slot < 0 || amount <= 0) {
                        return false;
                    }
                    player.getPriceChecker().withdraw(id, amount);
                    return true;
                }
            });
        }

        if (interfaceId == ShopUtility.ITEM_CHILD_ID) {
            Shop.exchange(player, id, slot, 5, true);
        }

        if (interfaceId == SHOP_INVENTORY) {
            Shop.exchange(player, id, slot, 5, false);
        }

        if (interfaceId == REMOVE_INVENTORY_ITEM) { // Duel/Trade inventory

            if (player.<Boolean>getAttribOr(USING_TRADING_POST, false)) {
                TradingPost.handleXOptionInput(player, id, slot);
            } else if (player.getStatus() == PlayerStatus.TRADING) {

                player.setAmountScript("How many would you like to offer?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        player.getTrading().handleItem(id, (Integer) value, slot, player.inventory(), player.getTrading().getContainer());
                        return true;
                    }
                });
            } else if (player.getStatus() == PlayerStatus.DUELING) {

                player.setAmountScript("How many would you like to offer?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int input = (Integer) value;
                        if (id < 0 || slot < 0 || input <= 0) {
                            return false;
                        }
                        player.getDueling().handleItem(id, input, slot, player.inventory(), player.getDueling().getContainer());
                        return true;
                    }
                });
                return;
            }
        }

        if (interfaceId == Trading.CONTAINER_INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.TRADING) {

                player.setAmountScript("How many would you like to remove?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int amount = (Integer) value;
                        if (id < 0 || slot < 0 || amount <= 0) {
                            return false;
                        }
                        player.getTrading().handleItem(id, amount, slot, player.getTrading().getContainer(), player.inventory());
                        return true;
                    }
                });
                return;
            }
        }

        if (interfaceId == Dueling.MAIN_INTERFACE_CONTAINER) {
            if (player.getStatus() == PlayerStatus.DUELING) {

                player.setAmountScript("How many would you like to remove?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int input = (Integer) value;
                        if (id < 0 || slot < 0 || input <= 0)
                            return false;
                        player.getDueling().handleItem(id, input, slot, player.getDueling().getContainer(), player.inventory());
                        return true;
                    }
                });
                return;
            }
        }
    }
}
