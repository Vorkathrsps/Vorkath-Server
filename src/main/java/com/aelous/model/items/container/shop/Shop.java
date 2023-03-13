package com.aelous.model.items.container.shop;

import com.aelous.GameServer;
import com.aelous.GameConstants;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.pets.PetDefinitions;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.shop.currency.CurrencyType;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * The container that represents a shop players can buy and sell items from.
 *
 * @author lare96 <http://github.com/lare96>
 */
public abstract class Shop {

    private static final Logger shopLogs = LogManager.getLogger("ShopsLogs");
    private static final Level SHOPS_LEVEL;

    public static void open(Player player, int id) {
        World.getWorld().shop(id).open(player);
    }

    static {
        SHOPS_LEVEL = Level.getLevel("SHOPS");
    }

    AddStockTask addStockTask;
    RemoveStockTask removeStockTask;

    /**
     * The id of this shop.
     */
    public int shopId;

    /**
     * The name of this shop.
     */
    public String name;

    /**
     * Can ironman access this shop.
     */
    public boolean noiron;

    /**
     * The current item container which contains the current items from this
     * shop.
     */
    public ItemContainer container;

    /**
     * The currency for this shop.
     */
    public CurrencyType currencyType;

    /**
     * The map of cached shop item identifications and their amounts.
     */
    public Map<Integer, Integer> itemCache;

    /**
     * The set of players that are currently viewing this shop.
     */
    public final Set<Player> players = new HashSet<>();

    public Shop(int shopId, String name, boolean noiron, ItemContainer.StackPolicy policy, CurrencyType currencyType, int capacity) {
        this.shopId = shopId;
        this.name = name;
        this.noiron = noiron;
        this.currencyType = currencyType;
        this.container = new ItemContainer(capacity, policy, new StoreItem[capacity]);
        this.itemCache = new HashMap<>(container.capacity());
    }

    public static void closeShop(Player player) {
        if (!player.getInterfaceManager().isInterfaceOpen(ShopUtility.SHOP_INTERFACE)) {
            return;
        }

        int id = player.getAttribOr(AttributeKey.SHOP, -1);

        Shop store = World.getWorld().shop(id);

        if (store == null) {
            return;
        }

        player.debugMessage("Closing store for " + player.toString() + ".");
        store.close(player);
    }

    public static void exchange(Player player, int id, int slot, int action, boolean purchase) {
        if (!player.getInterfaceManager().isInterfaceOpen(ShopUtility.SHOP_INTERFACE) && !player.getInterfaceManager().isInterfaceOpen(ShopUtility.SLAYER_SHOP_INTERFACE)) {
            return;
        }

        int shop = player.getAttribOr(AttributeKey.SHOP, -1);

        Shop store = World.getWorld().shop(shop);

        if (store == null) {
            return;
        }

        store.itemContainerAction(player, id, slot, action, purchase);

        if (action == 5 && shop != 7) {

            player.setAmountScript("How many would you like to " + (purchase ? "buy" : "sell") + "?", new InputScript() {

                @Override
                public boolean handle(Object value) {
                    player.putAttrib(AttributeKey.STORE_X, (Integer) value);
                    store.itemContainerAction(player, id, slot, action, purchase);
                    return true;
                }
            });
        }
    }

    public abstract void itemContainerAction(Player player, int id, int slot, int action, boolean purchase);

    public void purchase(Player player, Item item, int slot) {
    System.out.println("Amount:" + item.getAmount());
        if (!Item.valid(item)) {
            return;
        }

        player.clearAttrib(AttributeKey.STORE_X);

        Optional<Item> find = container.retrieve(slot);

        if (find.isEmpty()) {
            return;
        }

        Item found = find.get();

        if (!(found instanceof StoreItem)) {
            return;
        }

        if (!found.matchesId(item.getId())) {
            player.message("Something went wrong.");
            return;
        }

        StoreItem storeItem = (StoreItem) find.get();

        if (storeItem.getAmount() < 1) {
            player.message("There is none of this item left in stock!");
            return;
        }

        if (item.getAmount() > storeItem.getAmount() && shopId != 7)
            item.setAmount(storeItem.getAmount());

        if (!player.inventory().hasCapacity(item) && !item.noteable()) {
            item.setAmount(player.inventory().remaining());

            if (item.getAmount() == 0) {
                player.message("You don't have enough space in your inventory to buy this item!");
                return;
            }
        }

        int value = storeItem.getShopValue();
        long safetyCost = (1L * value * item.getAmount());
        if (safetyCost > Integer.MAX_VALUE) {
            int safeAmtToBuy = Math.max(0, (Integer.MAX_VALUE / value) - 1);
            item.setAmount(safeAmtToBuy);
        }
        int cost = (value * item.getAmount());
        if (!(currencyType.currency.currencyAmount(player, cost) >= cost)) {
            player.message("You don't have enough " + currencyType.toString() + " to buy this item.");
            return;
        }

        if (player.getInventory().remaining() >= item.getAmount() && !item.stackable() && !item.noteable()
            || player.getInventory().remaining() >= 1 && (item.stackable() || item.noteable())
            || player.getInventory().contains(item.getId()) && item.stackable()) {
            boolean canNote = item.noteable();
            int giveNotedItemOrUnnoted = canNote && item.getAmount() > 1 ? item.note().getId() : item.getId();

            if (GameServer.properties().pvpMode) {
                item = new Item(giveNotedItemOrUnnoted, item.getAmount());
            } else {
                item = new Item(item.getId(), item.getAmount());
            }

            if (value > 0 && !currencyType.currency.takeCurrency(player, item.getAmount() * value)) {
                return;
            }

            boolean ignore_amount = (GameServer.properties().pvpMode || shopId == 2) && shopId != 16;

            if (itemCache.containsKey(item.getId()) && container.retrieve(slot).isPresent() && !ignore_amount) {
                if (decrementStock()) {
                    container.retrieve(slot).get().decrementAmountBy(item.getAmount());
                }
            } else if (!itemCache.containsKey(item.getId())) {
                if (decrementStock()) {
                    container.remove(item);
                }
            }
        } else {
            player.message("You don't have enough space in your inventory.");
            return;
        }
        onPurchase(player, item);
        player.inventory().addOrBank(item);
        if (player.getInterfaceManager().isInterfaceOpen(ShopUtility.SLAYER_SHOP_INTERFACE)) {
            int slayerRewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
            player.getPacketSender().sendString(64014, "Reward Points: " + Utils.formatNumber(slayerRewardPoints));
        }

        shopLogs.log(SHOPS_LEVEL, player.getUsername() + " has bought " + item.unnote().name() + " from a shop for " + Utils.formatNumber((long) item.getAmount() * value) + " " + currencyType.currency.toString());
        Utils.sendDiscordInfoLog(player.getUsername() + " has bought " + item.unnote().name() + " from a shop for " + Utils.formatNumber((long) item.getAmount() * value) + " " + currencyType.currency.toString(), "shops");

        //Don't refresh the shop for one player, refresh it for all players.
        for (Player player1 : this.players) {
            refresh(player1, false);
        }
    }

    public void onPurchase(Player player, Item item) {
        if (item.getId() == ItemIdentifiers.BABY_CHINCHOMPA_13326) {
            if (!player.isPetUnlocked(PetDefinitions.BABY_CHINCHOMPA_YELLOW.varbit)) {
                player.addUnlockedPet(PetDefinitions.BABY_CHINCHOMPA_YELLOW.varbit);
            }
        }

        if (item.getId() == ItemIdentifiers.PET_SMOKE_DEVIL) {
            if (!player.isPetUnlocked(PetDefinitions.PET_SMOKE_DEVIL.varbit)) {
                player.addUnlockedPet(PetDefinitions.PET_SMOKE_DEVIL.varbit);
            }
        }

        if (item.getId() == ItemIdentifiers.HERB_BOX) {
            player.putAttrib(AttributeKey.HERB_BOX_CHARGES,20);
        }

        if(shopId == 4) {
            if (item.getId() == ANGLERFISH) {
                item.setId(ANGLERFISH + 1);
                item.setAmount(100);
            }

            if (item.getId() == SUPER_COMBAT_POTION4) {
                item.setId(SUPER_COMBAT_POTION4 + 1);
                item.setAmount(100);
            }

            if (item.getId() == OVERLOAD_4) {
                item.setAmount(100);
            }
        }

        if(shopId == 47) {
            if (item.getId() == CANNONBALL) {
                item.setAmount(1000);
            }
        }

        for (SkillcapeHoods skillcapeHoods : SkillcapeHoods.values()) {
            //Check if the item being purchased is a cape with hood.
            if (Arrays.stream(skillcapeHoods.getCapes()).anyMatch(id -> id == item.getId())) {
                //Add the hood in the players inventory if there is space, otherwise bank.
                player.inventory().addOrBank(new Item(skillcapeHoods.getHood()));
            }
        }
    }

    protected final void sell(Player player, Item item, int slot) {
        if (!Item.valid(item)) {
            return;
        }

        final Item inventoryItem = player.inventory().get(slot);

        player.clearAttrib(AttributeKey.STORE_X);

        if (inventoryItem == null) {
            player.message("This item does not exist.");
            return;
        }

        /*if (!item.rawtradable()) {
            player.message("This item can't be sold to shops.");
            return;
        }*/

        if (sellType() == SellType.NONE) {
            player.message("You can't sell items to this shop.");
            return;
        }

        if (item.getId() == COINS_995 || item.getId() == PLATINUM_TOKEN || item.getId() == BLOOD_MONEY) {
            player.message("You can't sell this item.");
            return;
        }

        for (Item bankItem : GameConstants.BANK_ITEMS) {
            if (bankItem.note().getId() == item.getId()) {
                player.message("You can't sell this item.");
                return;
            }
            if (bankItem.getId() == item.getId()) {
                player.message("You can't sell this item.");
                return;
            }
        }

/*        if (item.unnote().definition(World.getWorld()).pvpAllowed) {
            player.message("You can't trade spawnable items.");
            return;
        }*/

        if (Arrays.stream(GameConstants.DONATOR_ITEMS).anyMatch(id -> id == item.getId())) {
            player.message("You can't sell this item.");
            return;
        }

        if (sellType() == SellType.ANY && name.equalsIgnoreCase("General store")) {
            if (item.getValue() <= 0) {
                player.message("You can't sell items to this shop that have no value.");
                return;
            }
        }

        final boolean contains = container.contains(item.unnote().getId());

        if (!contains && sellType() == SellType.CONTAINS) {
            player.message("You can't sell " + item.unnote().name() + " to this shop.");
            return;
        }

        if (!container.hasCapacity(item.unnote())) {
            player.message("There is no room in this store for the item you are trying to sell!");
            return;
        }

        if (player.inventory().remaining() == 0 && !currencyType.currency.canRecieveCurrency(player)
            && inventoryItem.getAmount() > 1) {
            player.message("You do not have enough space in your inventory to sell this item!");
            return;
        }

        if (CurrencyType.isCurrency(item.getId())) {
            player.message("You can not sell currency to this shop!");
            return;
        }

        Optional<Item> find = container.search(item.getId());
        int sellValue;
        sellValue = item.getId() == 619 ? 1 : item.getSellValue();

        final int amount = player.inventory().count(item.getId());

        if (item.getAmount() > amount && !item.stackable()) {
            item.setAmount(amount);
        } else if (item.getAmount() > inventoryItem.getAmount() && item.stackable()) {
            item.setAmount(inventoryItem.getAmount());
        }

        //Safety
        if (player.inventory().count(item.getId()) < 1) {
            return;
        }

        player.inventory().remove(item, slot);

        if (sellValue > 0) {
            currencyType.currency.recieveCurrency(player, item.getAmount() * sellValue);
            player.message("You sold your " + item.unnote().name() + " for " + Utils.formatNumber((long) item.getAmount() * sellValue) + " " + currencyType.currency.toString() + ".");
            shopLogs.log(SHOPS_LEVEL, player.getUsername() + " has sold " + item.unnote().name() + " for " + Utils.formatNumber((long) item.getAmount() * sellValue) + " " + currencyType.currency.toString() + ".");
            Utils.sendDiscordInfoLog(player.getUsername() + " has sold " + item.unnote().name() + " for " + Utils.formatNumber((long) item.getAmount() * sellValue) + " " + currencyType.currency.toString() + ".", "shops");
        }
        StoreItem converted = new StoreItem(item.getId(), item.getAmount());

        boolean dontAddToContainer = GameServer.properties().pvpMode && shopId != 16;

        if (!dontAddToContainer) {
            if (find.isPresent()) {
                Item found = find.get();
                found.setAmount(found.getAmount() + item.getAmount());
            } else {
                container.add(converted);
            }
        }

        //Don't refresh the shop for one player, refresh it for all players.
        //refresh(player);
        for (Player player1 : this.players) {
            refresh(player1, false);
        }
    }

    public abstract void refresh(Player player, boolean redrawStrings);

    public void startAddStock() {
        if (addStockTask == null) {
            addStockTask = new AddStockTask(this);
            TaskManager.submit(addStockTask);
        }
    }

    public void startRemoveStock() {
        if (removeStockTask == null) {
            removeStockTask = new RemoveStockTask(this);
            TaskManager.submit(removeStockTask);
        }
    }

    protected final void sendSellValue(Player player, int slot) {
        Item item = player.inventory().get(slot);

        if (item == null) {
            return;
        }

        if ( item.getId() == COINS_995 || item.getId() == PLATINUM_TOKEN || item.getId() == BLOOD_MONEY) {
            player.message("This item can't be sold to shops.");
            return;
        }

        for (Item bankItem : GameConstants.BANK_ITEMS) {
            if (bankItem.note().getId() == item.getId()) {
                player.message("You can't sell this item.");
                return;
            }
            if (bankItem.getId() == item.getId()) {
                player.message("You can't sell this item.");
                return;
            }
        }

/*        if (item.unnote().definition(World.getWorld()).pvpAllowed) {
            player.message("You can't trade spawnable items.");
            return;
        }

        if (item.getValue() <= 0) {
            player.message("You can't sell items to this shop that have no value.");
            return;
        }*/

        if (Arrays.stream(GameConstants.DONATOR_ITEMS).anyMatch(id -> id == item.getId())) {
            player.message("This item can't be sold to shops.");
            return;
        }

        if (CurrencyType.isCurrency(item.getId())) {
            player.message("You can not sell currency to this shop!");
            return;
        }

        final boolean contains = container.contains(item.getId());

        if (!contains && sellType() == SellType.CONTAINS) {
            player.message("You can't sell " + item.unnote().name() + " to this shop.");
            return;
        }

        int value = item.getId() == 619 ? 1 : item.getSellValue();

        if (value <= 0) {
            if (this.sellType() != SellType.NONE) {
                player.message(String.format("%s will buy %s for free!", name, item.unnote().name()));
            } else {
                player.message(String.format("%s will not buy any items.", name));
            }
            return;
        }

        final String message = this.sellType() != SellType.NONE ? String.format("%s will buy %s for %s %s.", name,
            item.unnote().name(), Utils.formatNumber(value), currencyType.toString())
            : String.format("%s will not buy any items.", name);
        player.message(message);
    }

    protected void sendPurchaseValue(Player player, int slot) {
        Optional<Item> find = container.retrieve(slot);

        if (find.isEmpty()) {
            return;
        }

        Item item = find.get();

        if (item instanceof StoreItem) {
            StoreItem storeItem = (StoreItem) item;
            int value = storeItem.getShopValue();
            String message = Color.RED.tag() + "The shop will sell this " + item.unnote().name() + " for " + (value <= 0 ? "free!" : Utils.formatValue(value) + storeItem.getShopCurrency(this).toString() + ".");
            if (shopId == 47) {
                message = Color.RED.tag() + "The shop will sell x" + item.getAmount() + " " + item.unnote().name() + " for " + (value <= 0 ? "free!" : Utils.formatValue(value) + storeItem.getShopCurrency(this).toString() + ".");//Override message
            }
            player.message(message);
        }
    }

    public abstract void open(Player player);

    public abstract void close(Player player);

    public abstract SellType sellType();

    public boolean decrementStock() {
        return true;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Shop))
            return false;
        Shop other = (Shop) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
