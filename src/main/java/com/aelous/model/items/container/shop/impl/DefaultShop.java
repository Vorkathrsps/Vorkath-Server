package com.aelous.model.items.container.shop.impl;

import com.aelous.GameServer;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.shop.SellType;
import com.aelous.model.items.container.shop.Shop;
import com.aelous.model.items.container.shop.ShopUtility;
import com.aelous.model.items.container.shop.StoreItem;
import com.aelous.model.items.container.shop.currency.CurrencyType;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.Objects;

import static com.aelous.model.items.container.shop.ShopUtility.*;

/**
 * The default shop which are owned by the server.
 *
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @author <a href="http://www.rune-server.org/members/Zerikoth/">Zerikoth</a>
 */
public final class DefaultShop extends Shop {

    /**
     * The items in this shop.
     */
    public final StoreItem[] items;

    /**
     * The original item container this shop started with.
     */
    public final ItemContainer original;

    /**
     * Determines if this shop restocks.
     */
    public final boolean restock;

    public final SellType sellType;

    public final int scroll;

    /**
     * Creates a new {@link Shop}.
     *
     * @param items    the items in this container.
     * @param name     the name of this current shop.
     * @param noiron   Ironmen cant access this shop
     * @param sellType The different ways items can be sold to the shop.
     * @param restock  the flag that determines if this shop will restock its items.
     * @param currency the currency that items within this shop will be bought with.
     */
    public DefaultShop(StoreItem[] items, int shopId, String name, boolean noiron, SellType sellType, int scroll, boolean restock, CurrencyType currency) {
        super(shopId, name, noiron, ItemContainer.StackPolicy.ALWAYS, currency, sellType == SellType.ANY ? MAX_SHOP_ITEMS : items.length);
        this.items = items;
        this.restock = restock;
        this.sellType = sellType;
        this.scroll = scroll;
        this.original = new ItemContainer(items.length, ItemContainer.StackPolicy.ALWAYS);
        this.original.setItems(items, false);
        this.container.setItems(items, false);
        Arrays.stream(items).filter(Objects::nonNull).forEach(item -> itemCache.put(item.getId(), item.getAmount()));
    }

    /**
     * Determines if the items in the container need to be restocked.
     *
     * @return {@code true} if the items need to be restocked, {@code false}
     * otherwise.
     */
    protected boolean needsRestock() {
        return container.stream().filter(Objects::nonNull).anyMatch(i -> !itemCache.containsKey(i.getId()) || (itemCache.containsKey(i.getId()) && i.getAmount() < itemCache.get(i.getId())));
    }

    @Override
    public void itemContainerAction(Player player, int id, int slot, int action, boolean purchase) {
        //System.out.println("Current shop action: "+action);
        if (action == 1) {
            if (purchase) {
                this.sendPurchaseValue(player, slot);
            } else {
                this.sendSellValue(player, slot);
            }
        } else {
            int amount = 0;

            if (action == 2) {
                amount = 1;
            }

            if (action == 3) {
                amount = 5;
            }

            if (action == 4) {
                amount = 10;
            }

            if (action == 5) {
                amount = shopId == 7 ? 5 : player.getAttribOr(AttributeKey.STORE_X, 0);
            }

            if (action == 5 && shopId == 7) {
                amount = 10;
            }

            if (purchase) {
                if (amount > 0) {
                    this.purchase(player, new Item(id, amount), slot);
                }
            } else {
                if (amount > 0) {
                    this.sell(player, new Item(id, amount), slot);
                }
            }
        }
    }

    @Override
    public void open(Player player) {
        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if(player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (player.getIronManStatus() != IronMode.NONE && noiron) {
            player.message("Iron men cannot access this shop.");
            player.getInterfaceManager().closeDialogue();
            return;
        }

        player.putAttrib(AttributeKey.SHOP, shopId);

        if (!World.getWorld().shops.containsKey(shopId)) {
            World.getWorld().shops.put(shopId, this);
        }

        players.add(player);
        player.inventory().refresh();
        refresh(player, true);

        int rewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
        player.getPacketSender().sendString(64014, "Reward Points: " + Utils.formatNumber(rewardPoints));
        var showButtons = shopId == 4 || shopId == 5 || shopId == 18 || shopId == 43 || shopId == 44 || shopId == 45 || shopId == 12 || shopId == 10 || shopId == 11;
        player.getPacketSender().sendInterfaceDisplayState(28060, !showButtons);
        player.getPacketSender().sendString(shopId == 7 ? 64005 : ShopUtility.NAME_INTERFACE_CHILD_ID, name);
        player.getInterfaceManager().openInventory(shopId == 7 ? 64000 : ShopUtility.INTERFACE_ID, InterfaceConstants.SHOP_INVENTORY - 1);

        if(shopId == 4 || shopId == 5 || shopId == 18) {
            player.putAttrib(AttributeKey.CUSTOM_SHOP_ACTION,1);
            player.getPacketSender().sendString(28064, "BM Wares");
            player.getPacketSender().sendString(28065, "Barrows");
            player.getPacketSender().sendString(28066, "Other");
        } else if(shopId == 43 || shopId == 44 || shopId == 45) {
            player.putAttrib(AttributeKey.CUSTOM_SHOP_ACTION,2);
            player.getPacketSender().sendString(28064, "General");
            player.getPacketSender().sendString(28065, "Cosmetic");
            player.getPacketSender().sendString(28066, "Other");
        }
        else if(shopId == 12 || shopId == 10 || shopId == 11) {
            player.putAttrib(AttributeKey.CUSTOM_SHOP_ACTION,4);
            player.getPacketSender().sendString(28064, "  Melee");
            player.getPacketSender().sendString(28065, "  Range");
            player.getPacketSender().sendString(28066, "Magic");
        } else {
            player.putAttrib(AttributeKey.CUSTOM_SHOP_ACTION,0);
        }
    }


    @Override
    public void close(Player player) {
        players.remove(player);
        player.shopReference = ShopReference.DEFAULT;
        player.clearAttrib(AttributeKey.SHOP);
    }

    @Override
    public void refresh(Player player, boolean redrawStrings) {
        //Empty out the cost strings here at the top, that way it's cleared if it should be, and can be overwritten down below if necessary.
        if (redrawStrings) {
            for (int index = 0; index < MAX_SHOP_ITEMS; index++) {
                player.getPacketSender().sendString(AMOUNT_STRING_ID + index, "");
            }
        }
        final Item[] items = container.toArray();
        for (int index = 0; index < items.length; index++) {
            Item item = items[index];

            if (item == null) {
                continue;
            }

            if (item instanceof StoreItem) {
                if (redrawStrings) {
                    // Write the item cost string
                    final StoreItem storeItem = (StoreItem) items[index];

                    if (storeItem != null) {
                        int value = storeItem.getShopValue();
                        player.getPacketSender().sendString(shopId == 7 ? SLAYER_BUY_AMOUNT_STRING_ID + index : ShopUtility.AMOUNT_STRING_ID + index, value == 0 ? "FREE" : "" + Utils.formatRunescapeStyle(value));
                    }
                }
            }
        }

        player.getPacketSender().sendScrollbarHeight(shopId == 7 ? 64015 : ShopUtility.SCROLL_BAR_INTERFACE_ID, scroll);
        player.getPacketSender().sendItemOnInterface(3823, player.inventory().toArray());
        players.stream().filter(Objects::nonNull).forEach(p -> player.getPacketSender().sendItemOnInterface(shopId == 7 ? ShopUtility.SLAYER_BUY_ITEM_CHILD_ID : 3900, container.toArray()));
        if (restock) {
            if (!needsRestock()) {
                return;
            }
            //System.out.println("Oh we restocking");
            startAddStock();
            startRemoveStock();
        }

    }

    @Override
    public SellType sellType() {
        return sellType;
    }

}
