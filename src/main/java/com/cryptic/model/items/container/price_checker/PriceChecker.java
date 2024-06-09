package com.cryptic.model.items.container.price_checker;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.InterfaceID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.util.CombinedId;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.model.items.container.ItemContainerAdapter;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import java.util.Optional;

public class PriceChecker extends ItemContainer {

    /**
     * Holds all the string identifications
     */
    private final int[] STRINGS = {49550, 49551, 49552, 49553, 49554, 49555, 49556, 49557, 49558, 49559, 49560, 49561,
        49562, 49563, 49564, 49565, 49566, 49567, 49568, 49569, 49570, 49571, 49572, 49573, 49574, 49575, 49576,
        49577,};

    /**
     * The player instance.
     */
    public Player player;

    /**
     * The item being searched.
     */
    public Item searchedItem;

    /**
     * Creates a new <code>PriceChecker<code>.
     */
    public PriceChecker(Player player) {
        super(28, StackPolicy.STANDARD);
        this.player = player;
        addListener(new PriceCheckerListener());
    }

    /**
     * Closes the price checker interface.
     */
    public void close() {
        player.getPriceChecker().withdrawAll();
        player.getPriceChecker().searchedItem = null;
        player.putAttrib(AttributeKey.PRICE_CHECKING, false);
    }

    /**
     * Opens the price checker interface.
     */
    public void open() {
        refresh();
        player.putAttrib(AttributeKey.PRICE_CHECKING, true);
        player.getInterfaceManager().openInventory(InterfaceConstants.PRICE_CHECKER, 5063);
    }

    /**
     * Sets the calculating value of the price checker.
     */
    public void setValue() {
        refresh();
    }

    /**
     * Deposits an item into the price checker.
     */
    public void deposit(int slot, int amount) {
        Item item = player.inventory().get(slot);
        if (item == null)
            return;

        if (!item.rawtradable() && item.getValue() <= 0) {
            player.message("This item isn't tradeble!");
            return;
        }

        if (item.rawtradable() && item.getValue() <= 0) {
            player.message(Color.RED.wrap("This item is tradeble, but has no value! [Please report this to a staff member]!"));
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

    /**
     * Withdraws an item from the price checker.
     */
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
        if (!item.stackable() && amount > player.inventory().getFreeSlots()) {
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

    public void removeAllFromSlot(int id) {
        for (Item item : getItems()) {
            if (item == null) continue;
            if (item.getId() != id) continue;
            if (this.remove(item)) {
                player.inventory().add(item, -1, true);
            }
        }
        runClientScripts();
        refresh();
    }

    public void sendAllToSlot(int id) {
        Item[] items = player.inventory().toArray();
        for (int slot = 0; slot < items.length; slot++) {
            Item item = items[slot];
            if (item == null) continue;
            if (item.getId() != id) continue;
            sendItemToSlot(slot, item.getAmount());
        }
        runClientScripts();
        refresh();
    }

    /**
     * Deposits all the items into the price checker.
     */
    public void depositAll() {
        Item[] items = player.inventory().toArray();
        for (int slot = 0; slot < items.length; slot++) {
            Item item = items[slot];
            if (item == null) {
                continue;
            }

            sendItemToSlot(slot, item.getAmount());
        }
        refresh();
    }

    private void runClientScripts() {
        long totalValue = 0;
        final Object[] params = new Object[player.getPriceChecker().size()];
        for (int i = 0; i < params.length; i++) {
            final Item item = player.getPriceChecker().get(i);
            if (item == null) {
                params[i] = 0;
                continue;
            }
            final int valueOfStack = item.getValue();
            params[i] = item.getId() == 995 ? 1 : valueOfStack;
            totalValue += (long) item.getValue() * item.getAmount();
        }
        final long total = totalValue;
        player.getPacketSender().runClientScriptNew(785, params);
        player.getPacketSender().runClientScriptNew(600, 1, 1, 15, ComponentID.TOTAL_GUIDE_PRICE);
        player.getPacketSender().setItemMessage(new CombinedId(GameInterface.GUIDE_PRICE.getId(), 8).combinedId, 6512, 0);
        player.getPacketSender().setComponentText(GameInterface.GUIDE_PRICE.getId(), 12, "Total guide price:<br><col=ffffff>" + Utils.formatValueCommas(total) + "</col>");
    }

    /**
     * Withdraw all the items from the price checker.
     */
    public void withdrawAll() {
        for (Item item : getItems()) {
            if (item == null) continue;
            if (this.remove(item)) {
                player.inventory().add(item, -1, true);
            }
        }
        refresh();
    }

    public void removeItemFromSlot(int itemId, int amount) {
        this.withdraw(itemId, amount);
        runClientScripts();
    }

    public void sendItemToSlot(int slot, int amount) {
        this.deposit(slot, amount);
        runClientScripts();
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
        refreshInventory(player, InterfaceID.GUIDE_PRICE_INVENTORY);
    }

    @Override
    public void onRefresh() {
        player.inventory().refresh();
    }

    private final class PriceCheckerListener extends ItemContainerAdapter {

        PriceCheckerListener() {
            super(player);
        }

        @Override
        public int getWidgetId() {
            return GameInterface.GUIDE_PRICE.getId();
        }

        @Override
        public String getCapacityExceededMsg() {
            return "Your price checker is currently full!";
        }

        @Override
        public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index, boolean refresh) {
            if (refresh) {
                player.getPacketSender().sendUpdateInvPartial(InterfaceID.GUIDE_PRICE_INVENTORY, index, newItem.orElse(null));
            }
        }

        @Override
        public void bulkItemsUpdated(ItemContainer container) {
            refresh();
        }
    }
}
