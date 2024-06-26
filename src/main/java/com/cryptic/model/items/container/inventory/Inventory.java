package com.cryptic.model.items.container.inventory;

import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.ItemContainerAdapter;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.items.tradingpost.TradingPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An {@link ItemContainer} implementation that manages the inventory for a
 * {@link Player}.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Inventory extends ItemContainer {
    private static final Logger logger = LogManager.getLogger(Inventory.class);

    /** The size of all equipment instances. */
    public static final int SIZE = 28;

    /** The player instance for which this inventory applies to. */
    private final Player player;

    /** Creates a new {@link Inventory}. */
    public Inventory(Player player) {
        super(SIZE, StackPolicy.STANDARD);
        addListener(new InventoryListener(player));
        this.player = player;
    }

    /** Refreshes the players inventory. */
    public void sync() {
        refresh(player, InterfaceConstants.INVENTORY_INTERFACE);
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, Optional<String> message, List<Item> items) {
        boolean overflow = false;
        for (Item item : items) {
            if (item == null)
                continue;
            if (hasCapacity(item) && player.inventory().add(item)) {
                player.debug("added inv, not banking: %s", item);
            } else {
                action.accept(item);
                overflow = true;
                player.debug("addOr fail, doing OR %s", item);
            }
        }
        if (overflow) {
            message.ifPresent(player::message);
        }
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, Optional<String> message, Item... items) {
        addOrExecute(action, message, Arrays.asList(items));
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, String message, Item... items) {
        addOrExecute(action, Optional.of(message), Arrays.asList(items));
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, String message, List<Item> items) {
        addOrExecute(action, Optional.of(message), items);
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, List<Item> items) {
        addOrExecute(action, Optional.empty(), items);
    }

    /**
     * Attempts to deposit the {@code items} to the inventory, if inventory is full
     * it'll execute the {@code action} for the remaining items that were not added.
     */
    public void addOrExecute(Consumer<Item> action, Item... items) {
        addOrExecute(action, Arrays.asList(items));
    }

    /**
     * Attempts to deposit an item to the players inventory, if there is no space
     * it'll bank the item instead.
     */
    public void addOrDrop(List<Item> items) {
        addOrExecute(t -> GroundItemHandler.createGroundItem(new GroundItem(t, player.tile(), player)), "Some of the items were dropped beneath you instead...", items);
    }

    /**
     * Attempts to deposit an item to the players inventory, if there is no space
     * it'll bank the item instead.
     */
    public void addOrDrop(Item... items) {
        addOrDrop(Arrays.asList(items));
    }

    /**
     * Attempts to detect a shitlord duping or causing other issues.
     * @param item The {@link Item} to deposit.
     * @return
     */
    @Override
    public boolean add(Item item) {
        return super.add(item);
    }

    /**
     * Attempts to deposit an item to the players inventory, if there is no space
     * it'll bank the item instead.
     */
    public void addOrBank(List<Item> items) {
        addOrExecute(t -> player.getBank().depositFromNothing(t), "Some of the items were banked instead...", items);
    }
    /**
     * Attempts to deposit an item to the players inventory, if there is no space
     * it'll bank the item instead.
     */
    public void addOrBank(Item... items) {
        addOrBank(Arrays.asList(items));
    }

    /** Refreshes the inventory container. */
    @Override
    public void refresh(Player player, int widget) {
        player.getPacketSender().sendItemOnInterface(widget, toArray());
    }

    @Override
    public String toString() {
        return "{Inventory}=" + Arrays.toString(this.toNonNullArray());
    }

    /**
     * An {@link ItemContainerAdapter} implementation that listens for changes to
     * the inventory.
     */
    private final class InventoryListener extends ItemContainerAdapter {

        /** Creates a new {@link InventoryListener}. */
        InventoryListener(Player player) {
            super(player);
        }

        @Override
        public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index, boolean refresh) {
            //Don't queue updating the item, just don't flush the player's packet until the end of
            if (refresh) {

                player.getPacketSender().sendItemOnInterfaceSlot(getWidgetId(), newItem.orElse(null), index);
                // you get the point - put loads of UI checks here and re-send that specific inventory
                // osrs does this auto under the hood
                if (player.getInterfaceManager().isInterfaceOpen(TradingPost.SELL_ID) ||
                    player.getInterfaceManager().isInterfaceOpen(TradingPost.OVERVIEW)) {
                    // sell and overview use the @offer@ client container hence need manual re-send
                    TradingPost.sendOfferInventory(player, TradingPost.SELL_ID);
                }
                // buy UI doesnt need a refresh, its the normal inventory
            }

        }

        @Override
        public int getWidgetId() {
            return InterfaceConstants.INVENTORY_INTERFACE;
        }

        @Override
        public String getCapacityExceededMsg() {
            return "You do not have enough space in your inventory.";
        }
    }
}
