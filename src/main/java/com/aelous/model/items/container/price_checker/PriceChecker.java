package com.aelous.model.items.container.price_checker;

import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.ItemContainerAdapter;
import com.aelous.utility.Utils;

import java.text.NumberFormat;
import java.util.Optional;

public class PriceChecker extends ItemContainer {

    /** Holds all the string identifications */
    private final int[] STRINGS = { 49550, 49551, 49552, 49553, 49554, 49555, 49556, 49557, 49558, 49559, 49560, 49561,
        49562, 49563, 49564, 49565, 49566, 49567, 49568, 49569, 49570, 49571, 49572, 49573, 49574, 49575, 49576,
        49577, };

    /** The player instance. */
    public Player player;

    /** The item being searched. */
    public Item searchedItem;

    /** Creates a new <code>PriceChecker<code>. */
    public PriceChecker(Player player) {
        super(28, StackPolicy.STANDARD);
        this.player = player;
        addListener(new PriceCheckerListener());
    }

    /** Closes the price checker interface. */
    public void close() {
        player.getPriceChecker().withdrawAll();
        player.getPriceChecker().searchedItem = null;
        player.putAttrib(AttributeKey.PRICE_CHECKING, false);
    }

    /** Opens the price checker interface. */
    public void open() {
        refresh();
        player.putAttrib(AttributeKey.PRICE_CHECKING, true);
        player.getInterfaceManager().openInventory(InterfaceConstants.PRICE_CHECKER, 5063);
    }

    /** Sets the calculating value of the price checker. */
    public void setValue() {
        refresh();
    }

    /** Deposits an item into the price checker. */
    public void deposit(int slot, int amount) {
        Item item = player.inventory().get(slot);
        if (item == null)
            return;

        if (!item.rawtradable()) {
            player.message("This item isn't tradeble!");
            return;
        }

        int id = item.getId();

        int invAmount = player.inventory().count(id);

        if (invAmount < amount) {
            amount = invAmount;
        }

        setFiringEvents(false);
        add(id, amount);
        player.inventory().remove(item.getId(), amount);
        setFiringEvents(true);
        refresh();
    }

    /** Withdraws an item from the price checker. */
    public void withdraw(int itemId, int amount) {
        int slot = getSlot(itemId);
        if (itemId < 0)
            return;

        Item item = get(slot);
        if (item == null || itemId != item.getId()) {
            return;
        }

        int contains = count(itemId);

        if (contains < amount) {
            amount = contains;
        }

        int id = item.getId();
        setFiringEvents(false);
        if (!new Item(id).stackable() && amount > player.inventory().getFreeSlots()) {
            amount = player.inventory().getFreeSlots();
        }

        int slotId = player.inventory().getSlot(id);
        if (slotId != -1) {
            Item i = player.inventory().get(slotId);
            if (Integer.MAX_VALUE - i.getAmount() < amount) {
                amount = Integer.MAX_VALUE - i.getAmount();
                player.message("Your inventory didn't have enough space to withdraw all that!");
            }
        }

        if (remove(item.getId(), amount)) {
            player.inventory().add(id, amount);
            shift();
        }

        setFiringEvents(true);
        refresh();
    }

    /** Deposits all the items into the price checker. */
    public void depositAll() {
        Item[] items = player.inventory().toArray();
        for (int slot = 0; slot < items.length; slot++) {
            Item item = items[slot];
            if (item == null) {
                continue;
            }

            deposit(slot, item.getAmount());
        }
        refresh();
    }

    /** Withdraw all the items from the price checker. */
    public void withdrawAll() {
        for (Item item : getItems()) {
            if (item != null) {
                if (this.remove(item)) {
                    player.inventory().add(item, -1, false);
                }
            }
        }
        refresh();
    }

    public boolean buttonActions(int button) {
        switch (button) {
            //Close
            case 49502 -> {
                player.getInterfaceManager().close();
                return true;
            }
            //Deposit all
            case 49505 -> {
                depositAll();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public void sync() {
        refresh(player, InterfaceConstants.PRICE_CHECKER_DISPLAY_ID);
    }

    @Override
    public void onRefresh() {
        for (int index = 0; index < STRINGS.length; index++) {
            String value = "";

            if (getItems()[index] != null) {
                int price = getItems()[index].getValue();
                int amount = getItems()[index].getAmount();

                value = getItems()[index].stackable() ? Utils.format(amount) + " x " + Utils.formatPrice(price) + "<br>" + "= " + Utils.formatPrice(price * amount) : Utils.formatPrice(price);
            }

            player.getPacketSender().sendString(STRINGS[index], value);
        }

        player.inventory().refresh();
        player.getPacketSender().sendString(49582, "");
        player.getPacketSender().sendString(49583, "");
        player.getPacketSender().sendItemOnInterfaceSlot(49581, searchedItem, 0);
        player.getPacketSender().sendConfig(237, 1);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.INVENTORY_STORE, player.inventory().toArray());
        player.getPacketSender().sendString(49582, searchedItem == null ? "" : "<col=ffb000>" + searchedItem.name() + ":");
        player.getPacketSender().sendString(49583, searchedItem == null ? "" : Utils.format(searchedItem.getValue()));
        player.getPacketSender().sendString(49513, "" + NumberFormat.getInstance().format(containerValue()));
    }

    private final class PriceCheckerListener extends ItemContainerAdapter {

        PriceCheckerListener() {
            super(player);
        }

        @Override
        public int getWidgetId() {
            return 5063;
        }

        @Override
        public String getCapacityExceededMsg() {
            return "Your price checker is currently full!";
        }

        @Override
        public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index, boolean refresh) {
        }

        @Override
        public void bulkItemsUpdated(ItemContainer container) {
            refresh();
        }
    }
}
