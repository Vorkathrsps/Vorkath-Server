package com.cryptic.model.items.container.shop.impl;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.shop.SellType;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.container.shop.ShopUtility;
import com.cryptic.model.items.container.shop.StoreItem;
import com.cryptic.model.items.container.shop.currency.CurrencyType;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.Objects;

import static com.cryptic.model.items.container.shop.ShopUtility.*;

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
    private boolean needsRestock() {
        return container.stream().filter(Objects::nonNull).anyMatch(i -> !itemCache.containsKey(i.getId()) || (itemCache.containsKey(i.getId()) && i.getAmount() < itemCache.get(i.getId())));
    }

    @Override
    public void itemContainerAction(Player player, int id, int slot, int action, boolean purchase) {
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

        if (player.askForAccountPin()) {
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

        boolean isSpriteShop = shopId == 48 || shopId == 350;
        int specialShop = shopId == 7 ? 64000 : isSpriteShop ? ShopUtility.SPRITE_SHOP_INTERFACE : ShopUtility.INTERFACE_ID;
        int rewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
        player.getPacketSender().sendString(64014, "Reward Points: " + Utils.formatNumber(rewardPoints));
        int nameId = ShopUtility.NAME_INTERFACE_CHILD_ID;
        switch (shopId) {
            case 48, 350 -> nameId = 82005;
            case 7 -> nameId = 64005;
        }
        player.getPacketSender().sendString(nameId, name);
        player.getInterfaceManager().openInventory(specialShop, InterfaceConstants.SHOP_INVENTORY - 1);

    }


    @Override
    public void close(Player player) {
        players.remove(player);
        player.shopReference = ShopReference.DEFAULT;
        player.clearAttrib(AttributeKey.SHOP);
        player.getInterfaceManager().close();
    }

    @Override
    public void refresh(Player player, boolean redrawStrings) {
        int startSprite = 82006;
        if (redrawStrings) {
            for (int index = 0; index < 50; index++) {
                player.getPacketSender().sendInterfaceSpriteChange(startSprite + index, -2);
            }
            for (int index = 0; index < 50; index++) {
                player.getPacketSender().sendString(SPRITE_SHOP_STRING_ID + index, "");
            }
            for (int index = 0; index < MAX_SHOP_ITEMS; index++) {
                player.getPacketSender().sendString(AMOUNT_STRING_ID + index, "");
            }
        }
        boolean isSpriteShop = shopId == 48 || shopId == 350;
        final Item[] items = container.toArray();
        player.getPacketSender().sendInterfaceScrollReset(SHOP_INTERFACE);
        for (int index = 0; index < items.length; index++) {
            Item item = items[index];

            if (item == null) {
                continue;
            }
            if (isSpriteShop) {
                final int sprite = shopId == 48 ? 2192 : 2191; //TODO implement into shop build
                player.getPacketSender().sendInterfaceSpriteChange(startSprite + index, sprite);
            }

            if (item instanceof StoreItem) {
                if (redrawStrings) {

                    final StoreItem storeItem = (StoreItem) items[index];

                    if (storeItem != null) {
                        int value = storeItem.getShopValue();
                        if (isSpriteShop) {
                            player.getPacketSender().sendString(SPRITE_SHOP_STRING_ID + index, value == 0 ? "" : Utils.formatRunescapeStyle(value));
                        } else {
                            player.getPacketSender().sendString(shopId == 7 ? SLAYER_BUY_AMOUNT_STRING_ID + index : ShopUtility.AMOUNT_STRING_ID + index, value == 0 ? "FREE" : "" + Utils.formatRunescapeStyle(value));
                        }
                    }
                }
            }
        }
        int shopInventoryId = 73190;
        if (isSpriteShop) {
            shopInventoryId = 82004;
        }
        if (shopId == 7) {
            shopInventoryId = 64016;
        }
        if (!isSpriteShop) {
            player.getPacketSender().sendScrollbarHeight(shopId == 7 ? 64015 : ShopUtility.SCROLL_BAR_INTERFACE_ID, items.length * 11);
        }
        int finalShop = shopInventoryId;
        player.getPacketSender().sendItemOnInterface(3823, player.inventory().toArray());
        players.stream().filter(Objects::nonNull).forEach(p -> player.getPacketSender().sendItemOnInterface(finalShop, items));
        if (restock) {
            if (!needsRestock()) return;
            startAddStock();
            startRemoveStock();
        }

    }

    @Override
    public SellType sellType() {
        return sellType;
    }

}
