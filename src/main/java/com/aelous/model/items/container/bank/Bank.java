package com.aelous.model.items.container.bank;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.content.duel.Dueling;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.ItemContainerAdapter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handles the Bank container.
 *
 * @author Michael
 * @author Daniel
 */
public class Bank extends ItemContainer {

    public static final int FILLER_ID = 20594;

    /**
     * The size of all equipment instances.
     */
    public static final int SIZE = 816;

    /**
     * The tab amount array.
     */
    public int[] tabAmounts = new int[10];

    /**
     * TAB_SIZE is incorrect, OSRS doesn't have a maximum size per tabs.
     */
    public int TAB_SIZE = 36;

    public int TAB_AMOUNT = 10;

    /**
     * The player instance.
     */
    private final Player player;

    /**
     * The current bank tab.
     */
    public int bankTab = 0;

    /**
     * The noting flag.
     */
    public boolean noting = false;

    /**
     * The inserting flag.
     */
    public boolean inserting = false;

    /**
     * Always withdraws all items
     */
    public boolean quantityAll = false;

    /**
     * Always withdraws X amount of items
     */
    public boolean quantityX = false;

    /**
     * The current X quantity
     */
    public int currentQuantityX = 0;

    /**
     * Always withdraws ten items
     */
    public boolean quantityTen = false;

    /**
     * Always withdraws five items
     */
    public boolean quantityFive = false;

    /**
     * Always withdraws one item
     */
    public boolean quantityOne = true;

    /**
     * The place holder flag.
     */
    public boolean placeHolder;

    /**
     * The amount of placeholder items we currently have
     */
    public int placeHolderAmount = 0;

    /**
     * First item in tab
     */
    public boolean show_item_in_tab = true;

    /**
     * Digit (1,2,3)
     */
    public boolean show_number_in_tab = false;

    /**
     * Roman numeral (I, II, III)
     */
    public boolean show_roman_number_in_tab = false;

    /**
     * Disables and enables the incinerator
     */
    public boolean show_incinerator = false;

    /**
     * Disables and enables the equipment button
     */
    public boolean show_equipment_button = true;

    /**
     * Disables and enables the inventory button
     */
    public boolean show_inventory_button = true;

    /**
     * Constructs a new <code>Bank<code>.
     */
    public Bank(Player player) {
        super(SIZE, StackPolicy.ALWAYS);
        this.player = player;
        this.placeHolder = false;
        addListener(new BankListener());
    }

    private void refreshConfigs() {
        player.getPacketSender().sendConfig(750, show_item_in_tab ? 1 : 0);
        player.getPacketSender().sendConfig(304, inserting ? 1 : 0);
        player.getPacketSender().sendConfig(115, noting ? 1 : 0);
        player.getPacketSender().sendConfig(314, quantityAll ? 1 : 0);
        player.getPacketSender().sendConfig(315, quantityX ? 1 : 0);
        player.getPacketSender().sendConfig(316, quantityTen ? 1 : 0);
        player.getPacketSender().sendConfig(317, quantityFive ? 1 : 0);
        player.getPacketSender().sendConfig(320, quantityOne ? 1 : 0);
        player.getPacketSender().setWidgetActive(26101, player.getBank().placeHolder);
    }

    /**
     * Opens the bank itemcontainer.
     */
    public void open() {
        if (!player.getBankPin().enterPin(this::open)) {
            return;
        }
        if (player.getIronManStatus() == IronMode.ULTIMATE) {
            player.message("As an Ultimate Iron Man, you cannot use the bank.");
            return;
        }
        player.getPacketSender().sendString(InterfaceConstants.BANK_WIDGET + 5, "The Bank of Zaryte");
        //Set search button inactive by default
        player.getPacketSender().setWidgetActive(26102, false);
        player.getPacketSender().sendString(26019, "");
        player.getPacketSender().sendString(26018, "816");
        player.putAttrib(AttributeKey.BANKING, true);
        player.getInterfaceManager().openInventory(InterfaceConstants.BANK_WIDGET, InterfaceConstants.INVENTORY_STORE - 1);
        refreshConfigs();
        refresh();
    }

    /**
     * Closes the bank itemcontainer
     */
    public void close() {
        player.putAttrib(AttributeKey.BANKING, false);
    }

    /**
     * Refreshes the bank itemcontainer.
     */
    @Override
    public void sync() {
        player.inventory().refresh(player, InterfaceConstants.INVENTORY_STORE);
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.WITHDRAW_BANK, toArray());
        player.getPacketSender().sendBanktabs();
        player.inventory().refresh();
        player.getEquipment().refresh();
        player.getRunePouch().refresh();
        player.getRisk().update();
    }

    public int getTabForItem(int item) {
        int slot = getSlot(item);
        int tab = tabForSlot(slot);
        if (tab == -1) tab = 0;
        for (int counter = 0; counter < TAB_AMOUNT; counter++) {
            if (tabAmounts[counter] >= TAB_SIZE) {
                tab++;
            }
        }
        return tab;
    }

    /**
     * Handles the place holder option for the contianer.
     */
    public void placeHolder(int item, int slot) {
        boolean hold = placeHolder;
        placeHolder = true;
        setFiringEvents(false);
        withdraw(item, slot, Integer.MAX_VALUE);
        setFiringEvents(true);
        placeHolder = hold;
        refresh();
    }

    public void deposit(int slot, int amount) {
        if (!player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.BANK_WIDGET)) {
            return;
        }
        deposit(slot, amount, player.inventory());
    }

    /**
     * Deposits item into bank.
     * <br> WARNING logic has link to {@link #depositFromNothing(Item)} so some changes
     * may need to be in both places
     */
    public void deposit(int slot, int amount, ItemContainer fromIc) {
        Item item = fromIc.get(slot);
        if (item == null)
            return;
        int id = item.getId();

        boolean lootingBagEmpty = player.getLootingBag().isEmpty();
        if ((id == 11941 || id == 22586) && !lootingBagEmpty) {
            player.getInterfaceManager().openInventory(InterfaceConstants.BANK_WIDGET, InterfaceConstants.LOOTING_BAG_BANK_ID);
            player.getLootingBag().setBankStoring(true);
            player.getLootingBag().onRefresh();
            return;
        }

        int invAmount = fromIc.count(id);

        if (invAmount < amount) {
            amount = invAmount;
        }

        setFiringEvents(false);

        if (item.noted())
            id = item.unnote().getId();

        //System.out.println("deposit <"+id+"> - "+item+" unnoted: "+item.getUnnotedId());

        if (!contains(id)) {
            if (size() + 1 > capacity()) {
                player.message("Your bank is full! You need to clear some items from your bank.");
                setFiringEvents(true);
                return;
            }

            int destinationSlot = nextFreeSlot();
            int placeholderSlot = computeNextEmptyPlaceholder(id);
            if (placeHolder && placeholderSlot != -1) {
                //System.out.println("Setting item amount to 1 for slot "+ placeholderSlot);
                get(placeholderSlot).setAmount(1);
            } else {
                // item wasnt in bank before or has props. add a new item.
                // increase active tab size by 1
                changeTabAmount(bankTab, 1);
                // insert into tab 0 main first dont care about size of the tab
                add(new Item(id, amount), destinationSlot);

                int from = destinationSlot;
                // move it from tab 0 main into the active tab you are in
                int to = slotForTab(bankTab);
                swap(true, from, to, false);
            }
        } else {
            Item depositItem = get(getSlot(id));
            if (depositItem == null) return;
            if (Integer.MAX_VALUE - depositItem.getAmount() < amount) {
                amount = Integer.MAX_VALUE - depositItem.getAmount();
                player.message("Your bank didn't have enough space to deposit all that!");
            }
            depositItem.incrementAmountBy(amount);
        }

        if (amount > 0)
            fromIc.remove(item.getId(), amount);
        setFiringEvents(true);

        refresh();
    }

    /**
     * Withdraws item from bank.
     */
    public void withdraw(int itemId, int slot, int amount) {
        if (!player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.BANK_WIDGET)) {
            return;
        }
        if (itemId < 0 || slot < 0 || slot > capacity()) return;
        Item item = get(slot);
        if (item == null || itemId != item.getId())
            return;
        if (item.getAmount() == 0) {//Releasing place holders
            boolean hold = placeHolder;
            placeHolder = false;
            int tabSlot = getSlot(item.getId());
            int tab = tabForSlot(tabSlot);
            changeTabAmount(tab, -1);
            remove(item);
            shift();
            placeHolder = hold;
            placeHolderAmount--;
            refresh();
            return;
        }

        if (item.getAmount() < amount) {
            amount = item.getAmount();
        }

        int id = item.getId();
        //System.out.println("withdraw <"+id+"> - "+item);
        if (noting) {
            //System.out.println("We are noting");
            if (!item.noteable()) {
                player.message("This item cannot be withdrawn as a note.");
            } else {
                id = item.note().getId();
            }
        }

        setFiringEvents(false);
        if (!new Item(id).stackable() && amount > player.getInventory().getFreeSlots()) {
            amount = player.getInventory().getFreeSlots();
        } else if (item.stackable() && player.getInventory().getFreeSlots() == 0) {
            if (!player.getInventory().contains(id)) {
                amount = 0;
            } else if (player.getInventory().count(id) + amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE - player.getInventory().count(id);
            }
        }

        if (amount == 0) {
            player.message("You do not have enough inventory spaces to withdraw this item.");
            return;
        }

        //We have to add a check for these buttons, otherwise it's going to delete items!
        boolean pressedAnyWithdrawButtons = player.getBank().quantityOne || player.getBank().quantityFive || player.getBank().quantityTen || player.getBank().quantityX || player.getBank().quantityAll;
        if(pressedAnyWithdrawButtons && player.getInventory().isFull()) {
            player.message("You do not have enough inventory spaces to withdraw this item.");
            return;
        }

        int withdrawSlot = player.getInventory().getSlot(id);
        if (withdrawSlot != -1) {
            Item withdrawItem = player.getInventory().get(withdrawSlot);
            if (withdrawItem == null) return;
            if (Integer.MAX_VALUE - withdrawItem.getAmount() < amount) {
                amount = Integer.MAX_VALUE - withdrawItem.getAmount();
                player.message("Your inventory didn't have enough space to withdraw all that!");
            }
        }

        // the normal removal method, by id and amount
        if (remove(item.getId(), amount)) {
            player.getInventory().add(new Item(id, amount));
            // when an item is taken out of the bank completely, it removes one amount from the tab amounts array
            if (!contains(item.getId())) {
                int tab = tabForSlot(slot);
                changeTabAmount(tab, -1);
                shift();
            }
        }
        setFiringEvents(true);
        refresh();
    }

    /**
     * Collapses a tab and shifts other tabs one slot to the left.
     *
     * @param tab The initial tab.
     */
    public void collapse(int tab, boolean collapseAll) {
        if (!player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.BANK_WIDGET)) {
            return;
        }
        if (tab == 0 && collapseAll) {
            Arrays.fill(tabAmounts, 0);
            tabAmounts[0] = size();
            shift();
            return;
        }
        /* Move the remaining items to the main tab. */
        int tabAmount = tabAmounts[tab];
        if (tabAmount > 0)
            moveTab(tab, 0);

        /* Shift the remaining tabs to the left to fill the gap. */
        recursiveCollapse(tab);
        player.getPacketSender().sendConfig(211, bankTab = 0);
    }

    private void recursiveCollapse(int tab) {
        if (tab == tabAmounts.length - 1) return;
        moveTab(tab + 1, tab);
        recursiveCollapse(tab + 1);
    }

    private void moveTab(int tab, int toTab) {
        int tabAmount = tabAmounts[tab];
        int fromSlot = slotForTab(tab);
        int toSlot = slotForTab(toTab) + 1;
        tabAmounts[tab] -= tabAmount;
        tabAmounts[toTab] += tabAmount;

        setFiringEvents(false);
        for (int i = 0; i < tabAmount; i++) {
            swap(true, fromSlot, toSlot, false);
        }
        setFiringEvents(true);
    }

    /**
     * Changes the amount of items stored in a tab.
     *
     * @param tab    The tab to modify.
     * @param amount The amount to change.
     */
    public void changeTabAmount(int tab, int amount) {
        if (tab < 0 || tab >= tabAmounts.length) {
            return;
        }

        tabAmounts[tab] += amount;
        if (tabAmounts[tab] == 0) {
            collapse(tab, false);
        }
    }

    /**
     * WARNING logic has link to {@link #remove(Item, int, boolean, boolean)} so some changes
     * may need to be in both places
     *
     * @return The count of any left-overs that could not be inserted, such as left-over from a full 2.1b stack.
     * <br> With this count, you should write some new code to deal with any remaining leftovers or bank them.
     * <br> see {@link Dueling#onDeath()} for an example of dropping the reaminder to the ground
     * <p>
     *
     */
    /**
     * WARNING logic has link to {@link #remove(Item, int, boolean, boolean)} so some changes
     * may need to be in both places
     *
     * @return The count of any left-overs that could not be inserted, such as left-over from a full 2.1b stack.
     * <br> With this count, you should write some new code to deal with any remaining leftovers or bank them.
     * <br> see {@link Dueling#onDeath()} for an example of dropping the remainder to the ground
     * <p>
     *
     */
    public Result depositFromNothing(Item item) {
        //if (player.getGameMode(GameMode.TRAINED_ACCOUNT).isUltimateIronman()) {
         //   return new Result(item.getAmount(), 0, 0);
        //}
        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, item.getId());
        if(def == null) {
            return new Result(item.getAmount(), 0, 0);
        }

        setFiringEvents(false);
        item = item.copy();
        int id = item.unnote().getId();
        int destinationSlot = nextFreeSlot();
        int placeholderSlot = computeNextEmptyPlaceholder(id);
        if (!contains(id)) {
            if (size() + 1 > capacity()) {
                return new Result(item.getAmount(), 0, 0);
            }

            if (placeHolder && placeholderSlot != -1) {
                //System.out.println("Setting item amount to 1 for slot "+ placeholderSlot);
                get(placeholderSlot).setAmount(1);
            } else {
                // increase active tab size by 1
                changeTabAmount(bankTab, 1);
                // insert into tab 0 main first don't care about size of the tab
                add(new Item(id, item.getAmount()), destinationSlot);

                // move it from tab 0 main into the active tab you are in
                int to = slotForTab(bankTab);
                swap(true, destinationSlot, to, false);
            }
            setFiringEvents(true);
            return new Result(item.getAmount(), item.getAmount(), destinationSlot); // full amount banked
        } else {
            var slot = getSlot(id);
            Item existing = get(slot);
            if (existing == null) return new Result(item.getAmount(), 0, 0);

            if ((long) existing.getAmount() + item.getAmount() > Integer.MAX_VALUE) {
                var amtToBank = (Integer.MAX_VALUE - existing.getAmount());
                int leftOverUnbanked = item.getAmount() - amtToBank;

                existing.setAmount(Integer.MAX_VALUE);

                //drop the rest (how many were left after as much as possible has been inserted)
                player.inventory().addOrDrop(new Item(item.getId(), leftOverUnbanked));
                setFiringEvents(true);
                return new Result(item.getAmount(), item.getAmount() - amtToBank, slot); // amount added
            } else {
                existing.incrementAmountBy(item.getAmount());
                setFiringEvents(true);
                return new Result(item.getAmount(), item.getAmount(),0); // full amount successfully banked
            }
        }
    }

    /**
     * Handles depositing the entire inventory.
     */
    public void depositInventory() {
        for (int i = 0; i <= 27; i++) {
            var itemAt = player.inventory().get(i);
            if(itemAt == null) continue; // Get item or continue
                deposit(i, itemAt.getAmount(), player.inventory());
        }
    }

    /**
     * Handles depositing all the equipment.
     */
    public void depositeEquipment() {
        for (int i = 0; i <= 13; i++) {
            var itemAt = player.getEquipment().get(i);
            if(itemAt == null) continue; // Get item or continue
            deposit(i, itemAt.getAmount(), player.getEquipment());
        }

        //Cancel auto cast spells
        Autocasting.setAutocast(player,null);
        player.getCombat().setRangedWeapon(null);
        player.getEquipment().login();
    }

    private void itemToTab(int slot, int toTab) {
        int fromTab = tabForSlot(slot);

        /* Item is already in this tab. */
        if (fromTab == toTab) return;

        /*
         * The tab to the left of the chosen tab is empty,
         * so don't create a new tab.
         */
        if (toTab > 1 && tabAmounts[toTab - 1] == 0 && tabAmounts[toTab] == 0) {
            return;
        }

        tabAmounts[toTab]++;
        tabAmounts[fromTab]--;
        int toSlot = slotForTab(toTab);

        if (tabAmounts[fromTab] == 0) {
            collapse(fromTab, false);
        }

        swap(true, slot, toSlot, false);
        refresh();
    }

    /**
     * Moves an item within the bank. <p> An opcode of 0 performs a swap
     * operation, with {@code from} being the origin slot and {@code to} being
     * the destination slot. </p> <p> An opcode of 1 performs an insert
     * operation, with {@code from} being the origin slot and {@code to} being
     * the destination slot. </p> <p> An opcode of 2 moves an item from one tab
     * to another, with {@code from} being the origin <b>slot</b> and {@code to}
     * being the destination <b>tab</b>. </p>
     *
     * @param opcode The opcode.
     * @param from   The origin slot or tab.
     * @param to     The destination slot or tab.
     */
    public void moveItem(int opcode, int from, int to) {
        if (opcode == 2) {
            itemToTab(from, to);
        } else if (opcode == 1) {
            swap(true, from, to, false);
            int fromTab = tabForSlot(from);
            int toTab = tabForSlot(to);
            if (fromTab != toTab) {
                changeTabAmount(toTab, 1);
                changeTabAmount(fromTab, -1);
                refresh();
            }
        } else {
            swap(from, to);
        }
    }

    public int tabForSlot(int slot) {
        if (slot <= -1)
            return -1;
        int passed = -1;
        for (int tab = 0; tab < tabAmounts.length; tab++) {
            if (slot <= passed + tabAmounts[tab])
                return tab;
            passed += tabAmounts[tab];
        }
        return -1;
    }

    private int slotForTab(int tab) {
        int passed = -1;
        for (int index = tab; index >= 0; index--) {
            passed += tabAmounts[index];
        }
        return passed;
    }

    @Override
    public void clear() {
        // reset tabs to all 0
        Arrays.fill(tabAmounts, 0);
        super.clear();
    }

    @Override
    public void shift() {
        // save old tab amounts
        int[] amounts = Arrays.copyOf(tabAmounts, tabAmounts.length);
        super.shift();
        // shift will have changed tabAmounts (by calling setItems > clear > Wipes all), so restore the old value.
        amounts = Arrays.copyOf(amounts, amounts.length);
        System.arraycopy(amounts, 0, tabAmounts, 0, amounts.length);
    }

    @Override
    public boolean allowZero() {
        //TODO we need to have a check here if placeholder already exists don't add up the total amt
        placeHolderAmount = placeHolderAmount + 1;
        return placeHolder;
    }

    @Override
    public String toString() {
        return "{Bank}=" + Arrays.toString(this.toNonNullArray());
    }

    public void removeFromBank(Item item) {
        int tabSlot = player.getBank().getSlot(item.getId());
        int tab = player.getBank().tabForSlot(tabSlot);
        item = player.getBank().get(tabSlot);
        //Item cannot be found
        if (item == null || tabSlot <= -1 || tab <= -1 || item.getAmount() <= 0) {
            return;
        }

        //Item was found, remove.
        if (player.getBank().remove(item, tabSlot, true)) {
            if (!player.getBank().indexOccupied(tabSlot)) {
                player.getBank().changeTabAmount(tab, -1);
                player.getBank().shift();
            }

            if (item.getAmount() < item.getAmount()) {
                item.setAmount(item.getAmount());
            }
        }
    }

    private final class BankListener extends ItemContainerAdapter {

        BankListener() {
            super(player);
        }

        @Override
        public int getWidgetId() {
            return InterfaceConstants.WITHDRAW_BANK;
        }

        @Override
        public String getCapacityExceededMsg() {
            return "Your bank is currently full!";
        }

        @Override
        public void itemUpdated(ItemContainer container, Optional<Item> oldItem, Optional<Item> newItem, int index, boolean refresh) {
        }

        @Override
        public void bulkItemsUpdated(ItemContainer container) {
            refresh();
        }
    }

    /**
     * The bank container buttons
     *
     * @param button The buttons we can click
     */
    public boolean buttonAction(int button) {
        if (!player.getInterfaceManager().isInterfaceOpen(InterfaceConstants.BANK_WIDGET) && !player.getInterfaceManager().isInterfaceOpen(34000))
            return false;
        if (button >= 26031 && button <= 26068) {
            final int tab = (26031 - button) / -4;
            if (button % 2 == 0) {
                player.getBank().bankTab = tab;
                player.getPacketSender().sendString(26019, "");
            } else {
                player.getBank().collapse(tab, tab == 0);
                player.getBank().refresh();
            }
            return true;
        }
        switch (button) {
            case 26102:
                // client sided search btn does nothing here.
                return true;

            case 26106:
                openSettings();
                return true;

            case 26119:
                player.getInterfaceManager().open(15106);
                player.getPacketSender().sendInterfaceDisplayState(15150, false);
                return true;

           /* case 34035: player.putAttrib(FILLER_AMT, 1);
                return true;
            case 34033:
                player.putAttrib(FILLER_AMT, 10);
                return true;
            case 34031:
                player.putAttrib(FILLER_AMT, 50);
                return true;
            case 34029:
                player.setEnterSyntax(new EnterSyntax() {
                    @Override
                    public void handleSyntax(Player player, int input) {
                        player.putAttrib(FILLER_AMT, Math.max(0, Math.min(getFreeSlots(), input)));
                    }
                });
                player.getPacketSender().sendEnterInputPrompt("How many slots do with want to fill with bank fillers?");
                return true;
            case 34027:
                player.putAttrib(FILLER_AMT, getFreeSlots());
                return true;

            case 34037:
                player.getBank().setFiringEvents(false);
                int filled = 0;
                for (int i = 0; i < capacity(); i++) {
                    if (get(i) == null) {
                        set(i, new Item(FILLER_ID, 1), false);
                        filled++;
                    }
                }
                player.getBank().setFiringEvents(true);
                player.getBank().refresh();
                player.message("You fill your bank with "+filled+"/"+player.getAttribOr(FILLER_AMT, 1)+" bank fillers.");
                return true;*/

            // Release Placeholders
            case 26072:
            case 34024:
                int count = 0;
                boolean toggle = player.getBank().placeHolder;
                player.getBank().placeHolder = false;
                player.getBank().setFiringEvents(false);
                for (Item item : player.getBank().toArray()) {
                    if (item != null && item.getAmount() == 0) {
                        int slot = player.getBank().getSlot(item.getId());
                        int tab = player.getBank().tabForSlot(slot);
                        player.getBank().changeTabAmount(tab, -1);
                        player.getBank().remove(item);
                        player.getBank().shift();
                        player.getBank().placeHolderAmount -= player.getBank().placeHolderAmount - count;
                        count++;
                    }
                }
                player.getBank().placeHolder = toggle;
                player.getBank().setFiringEvents(true);
                player.getBank().refresh();
                player.message(count == 0 ? "You don't have any placeholders to release." : "You have released " + count + " placeholders.");
                player.getPacketSender().sendString(34024, "Release all placeholders (" + player.getBank().placeHolderAmount + ")");
                return true;

            // Placeholders
            case 26101:
                boolean active = player.getBank().placeHolder = !player.getBank().placeHolder;
                player.getPacketSender().setWidgetActive(26101, active);
                return true;

            /* Deposit Inventory */
            case 26103:
                depositInventory();
                return true;

            /* Deposit Equipment */
            case 26104:
                depositeEquipment();
                return true;

            case 5386:
                noting = true;
                return true;

            case 5387:
                noting = false;
                return true;

            case 8130:
                inserting = false;
                return true;

            case 8131:
                inserting = true;
                return true;

            /* Close Bank */
            case 26002:
                player.getInterfaceManager().close();
                return true;

            case 26905:
                player.getLootingBag().depositLootingBag();
                break;

            case 26108: // Quantity all
                quantityAll = true;
                quantityOne = false;
                quantityFive = false;
                quantityTen = false;
                quantityX = false;
                player.getPacketSender().sendConfig(314, 1);
                player.getPacketSender().sendConfig(315, 0);
                player.getPacketSender().sendConfig(316, 0);
                player.getPacketSender().sendConfig(317, 0);
                player.getPacketSender().sendConfig(320, 0);
                return true;

            case 26109: // Quantity X
                player.setAmountScript("How many would you like to deposit/withdraw?", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int input = (Integer) value;
                        player.getBank().currentQuantityX = input == 0 ? 1 : (int) Math.min(input, Integer.MAX_VALUE);
                        player.getPacketSender().updateWidgetTooltipText(26109, "Default quantity: " + player.getBank().currentQuantityX);

                        return true;
                    }
                });
                quantityX = true;
                quantityOne = false;
                quantityFive = false;
                quantityTen = false;
                quantityAll = false;
                player.getPacketSender().sendConfig(314, 0);
                player.getPacketSender().sendConfig(315, 1);
                player.getPacketSender().sendConfig(316, 0);
                player.getPacketSender().sendConfig(317, 0);
                player.getPacketSender().sendConfig(320, 0);
                break;

            case 26110: // Quantity ten
                quantityTen = true;
                quantityOne = false;
                quantityFive = false;
                quantityX = false;
                quantityAll = false;
                player.getPacketSender().sendConfig(314, 0);
                player.getPacketSender().sendConfig(315, 0);
                player.getPacketSender().sendConfig(316, 1);
                player.getPacketSender().sendConfig(317, 0);
                player.getPacketSender().sendConfig(320, 0);
                break;

            case 26111: // Quantity five
                quantityFive = true;
                quantityOne = false;
                quantityTen = false;
                quantityX = false;
                quantityAll = false;
                player.getPacketSender().sendConfig(314, 0);
                player.getPacketSender().sendConfig(315, 0);
                player.getPacketSender().sendConfig(316, 0);
                player.getPacketSender().sendConfig(317, 1);
                player.getPacketSender().sendConfig(320, 0);
                break;

            case 26112: //Quantity one
                quantityOne = true;
                quantityFive = false;
                quantityTen = false;
                quantityX = false;
                quantityAll = false;
                player.getPacketSender().sendConfig(314, 0);
                player.getPacketSender().sendConfig(315, 0);
                player.getPacketSender().sendConfig(316, 0);
                player.getPacketSender().sendConfig(317, 0);
                player.getPacketSender().sendConfig(320, 1);
                break;

            case 34004:
                player.getBank().open();
                break;

            case 34016:
            case 34017:
            case 34018:
            case 34019:
            case 34020:
            case 34021:
                player.message("This option is unavailable.");
                break;

            case 34008:
            case 34009:
                player.getBank().show_item_in_tab = !player.getBank().show_item_in_tab;
                player.getPacketSender().sendConfig(750, show_item_in_tab ? 1 : 0);

                //Reset other options
                player.getBank().show_number_in_tab = false;
                player.getPacketSender().sendConfig(751, 0);
                player.getBank().show_roman_number_in_tab = false;
                player.getPacketSender().sendConfig(752, 0);
                break;

            case 34010:
            case 34011:
                player.getBank().show_number_in_tab = !player.getBank().show_number_in_tab;
                player.getPacketSender().sendConfig(751, show_number_in_tab ? 1 : 0);

                //Reset other options
                player.getBank().show_item_in_tab = false;
                player.getPacketSender().sendConfig(750, 0);
                player.getBank().show_roman_number_in_tab = false;
                player.getPacketSender().sendConfig(752, 0);
                break;

            case 34012:
            case 34013:
                player.getBank().show_roman_number_in_tab = !player.getBank().show_roman_number_in_tab;
                player.getPacketSender().sendConfig(752, show_roman_number_in_tab ? 1 : 0);

                //Reset other options
                player.getBank().show_item_in_tab = false;
                player.getPacketSender().sendConfig(750, 0);
                player.getBank().show_number_in_tab = false;
                player.getPacketSender().sendConfig(751, 0);
                break;
        }

        return false;
    }

    private void openSettings() {
        player.getInterfaceManager().open(34000);

        player.getPacketSender().sendConfig(750, show_item_in_tab ? 1 : 0); //Item in tab
        player.getPacketSender().sendConfig(751, show_number_in_tab ? 1 : 0); //Number in tab
        player.getPacketSender().sendConfig(752, show_roman_number_in_tab ? 1 : 0); //Roman number in tab
        player.getPacketSender().sendConfig(754, show_incinerator ? 1 : 0); //Incinerator
        player.getPacketSender().sendConfig(755, show_equipment_button ? 1 : 0); //'Deposit worn items' button
        player.getPacketSender().sendConfig(756, show_inventory_button ? 1 : 0); //'Deposit inventory' button

        player.getPacketSender().sendString(34024, "Release all placeholders (" + player.getBank().placeHolderAmount + ")");
    }

    /**
     * Computes the next empty placeholder ({@code null}) index in this container.
     *
     * @param id The item ID for the placeholder
     * @return The free index, {@code -1} if no free indexes could be found.
     */
    public final int computeNextEmptyPlaceholder(int id) {
        for (int index = 0; index < capacity(); index++) {
            if (get(index) != null && get(index).getId() == id && get(index).getAmount() == 0) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Computes the next used placeholder ({@code null}) index in this container.
     *
     * @param id The item ID for the placeholder
     * @return The free index, {@code -1} if no free indexes could be found.
     */
    public final int computeNextUsedPlaceholder(int id) {
        for (int index = 0; index < capacity(); index++) {
            if (get(index) != null && get(index).getId() == id && get(index).getAmount() == 1) {
                return index;
            }
        }
        return -1;
    }
}
