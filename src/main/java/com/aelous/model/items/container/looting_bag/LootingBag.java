package com.aelous.model.items.container.looting_bag;

import com.aelous.GameConstants;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.container.ItemContainerAdapter;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.CustomItemIdentifiers;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aelous.utility.ItemIdentifiers.LOOTING_BAG;
import static com.aelous.utility.ItemIdentifiers.LOOTING_BAG_22586;

/**
 * This class handles the functionality of the looting bag.
 *
 * @author Patrick van Elderen | 19:49 : vrijdag 5 juli 2019 (CEST)
 * @author Adam_#6723 For references, credits are given where due.
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class LootingBag extends ItemContainer {

    private static final Logger lootingBagLogs = LogManager.getLogger("LootingBagLogs");
    private static final Level LOOTING_BAG_LOGS;

    static {
        LOOTING_BAG_LOGS = Level.getLevel("LOOTING_BAG");
    }

    /**
     * The size of the container
     */
    public static final int SIZE = 28;

    /**
     * The string which sends the total value amount to the bag
     */
    private static final int VALUE_STRING = 26707;
    private static final int BAG_VALUE_STRING = 26807;

    /**
     * The string which is sent when the bag is empty
     */
    private static final int EMPTY_STRING = 26708;

    /**
     * The string which is sent when the bag is empty - Bank interface
     */
    private static final int BANK_EMPTY_STRING = 26908;

    private boolean askHowManyToStore;

    public void setAskHowManyToStore(boolean b) {
        this.askHowManyToStore = b;
    }

    public boolean askHowManyToStore() {
        return askHowManyToStore;
    }

    private boolean storeAsMany;

    public boolean storeAsMany() {
        return storeAsMany;
    }

    public void setStoreAsMany(boolean b) {
        this.storeAsMany = b;
    }

    private boolean depositing = false;

    private boolean bankStoring = false;

    public void setBankStoring(boolean b) {
        this.bankStoring = b;
    }

    /**
     * The player instance.
     */
    private final Player player;

    /**
     * Constructs a new <code>LootingBag</code>.
     *
     * @param player The player instance.
     */
    public LootingBag(Player player) {
        super(SIZE, StackPolicy.STANDARD);
        this.addListener(new LootingBagListener(player));
        this.player = player;
    }

    /**
     * Opens the looting bag widget
     */
    public void open() {
        player.getInterfaceManager().setSidebar(GameConstants.INVENTORY_TAB, InterfaceConstants.LOOTING_BAG_ID);
        player.getPacketSender().sendTab(GameConstants.INVENTORY_TAB);
        onRefresh();
    }

    /**
     * Opens the looting bag depositing widget
     */
    public void depositWidget() {
        depositing = true;
        onRefresh();
        player.getInterfaceManager().setSidebar(GameConstants.INVENTORY_TAB, InterfaceConstants.LOOTING_BAG_DEPOSIT_ID);
        player.getPacketSender().sendTab(GameConstants.INVENTORY_TAB);
    }

    /**
     * closes the looting bag.
     */
    public void close() {
        boolean banking = player.getAttribOr(AttributeKey.BANKING, false);
        if (banking) {
            player.getInterfaceManager().openInventory(InterfaceConstants.BANK_WIDGET, InterfaceConstants.INVENTORY_STORE - 1);
        }
        depositing = false;
        bankStoring = false;
        player.getInterfaceManager().setSidebar(GameConstants.INVENTORY_TAB, InterfaceConstants.INVENTORY_INTERFACE - 1);
    }

    /**
     * Drops all items on death
     */
    public Item[] dropItemsOnDeath() {
        if (!player.inventory().contains(11941) && !player.inventory().contains(22586))
            return null;
        return toNonNullArray();
    }

    /**
     * Change the looting bag when using the "open" option
     *
     * @param bag_id The bag we're changing
     */
    public void openAndCloseBag(int bag_id) {
        switch (bag_id) {
            case 11941 -> {
                player.inventory().replace(11941, 22586, true);
                player.message("You close your looting bag.");
            }
            case 22586 -> {
                player.inventory().replace(22586, 11941, true);
                player.message("You open your looting bag, ready to fill it.");
            }
        }
    }

    /**
     * Opens the settings dialogue
     */
    public void setSettings() {
        if (!player.inventory().contains(11941))
            return;
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... options) {
                send(DialogueType.OPTION, "When using items on the bag...", "... ask how many to store.", "... always store as many as possible.");
                setPhase(0);
            }

            @Override
            public void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        setAskHowManyToStore(true);
                        player.message("When using items on the bag, you will be asked how many of that item you wish to");
                        player.message("store in the bag.");
                        stop();
                    } else if (option == 2) {
                        setStoreAsMany(true);
                        player.message("When using items on the bag, you will immediately store as many of that item as");
                        player.message("possible in the bag.");
                        stop();
                    }
                }
            }
        });
    }

    /**
     * open the looting bag menu.
     *
     * @param item The item we're storing
     */
    public void depositMenu(Item item) {
        if (!player.inventory().contains(11941))
            return;

        if (item.getAmount() == 2) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    send(DialogueType.OPTION, "How many do you want to deposit?", "One", "Both");
                    setPhase(0);
                }

                @Override
                public void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 1, null);
                            stop();
                        } else if (option == 2) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 2, null);
                            stop();
                        }
                    }
                }
            });
        } else if (item.getAmount() == 3) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    send(DialogueType.OPTION, "How many do you want to deposit?", "One", "Two", "All");
                    setPhase(0);
                }

                @Override
                public void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 1, null);
                            stop();
                        } else if (option == 2) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 2, null);
                            stop();
                        } else if (option == 3) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, player.inventory().count(item.getId()), null);
                            stop();
                        }
                    }
                }
            });
        } else if (item.getAmount() >= 3 && item.getAmount() < 5) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    send(DialogueType.OPTION, "How many do you want to deposit?", "One", "All", "X");
                    setPhase(0);
                }

                @Override
                public void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 1, null);
                            stop();
                        } else if (option == 2) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, player.inventory().count(item.getId()), null);
                            stop();
                        } else if (option == 3) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            stop();

                            player.setAmountScript("How many would you like to deposit?", new InputScript() {

                                @Override
                                public boolean handle(Object value) {
                                    int itemId = item.getId();
                                    int slot = player.inventory().getSlot(itemId);
                                    int amount = (Integer) value;

                                    if (itemId < 0 || slot < 0 || amount <= 0) {
                                        return false;
                                    }

                                    if (!player.getLootingBag().contains(itemId)) {
                                        return false;
                                    }

                                    if (amount > player.getLootingBag().count(itemId))
                                        amount = player.getLootingBag().count(itemId);

                                    player.getLootingBag().withdrawBank(new Item(itemId, amount), slot);
                                    return true;
                                }
                            });
                        }
                    }
                }
            });
        } else {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    send(DialogueType.OPTION, "How many do you want to deposit?", "One", "Five", "All", "X");
                    setPhase(0);
                }

                @Override
                public void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 1, null);
                            stop();
                        } else if (option == 2) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, 5, null);
                            stop();
                        } else if (option == 3) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            deposit(item, Integer.MAX_VALUE, null);
                            stop();
                        } else if (option == 4) {
                            if (!player.inventory().contains(item)) {
                                stop();
                                return;
                            }
                            stop();

                            player.setAmountScript("How many would you like to deposit?", new InputScript() {

                                @Override
                                public boolean handle(Object value) {
                                    int amount = (Integer) value;
                                    int itemId = item.getId();

                                    if (itemId < 0 || player.inventory().getSlot(itemId) < 0 || amount <= 0) {
                                        return false;
                                    }
                                    if (!player.inventory().contains(itemId)) {
                                        return false;
                                    }
                                    player.getLootingBag().deposit(new Item(itemId, amount), amount, null);
                                    return true;
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public boolean deposit(Item item, int amount, GroundItem groundItem) {
        if (item.getId() == CustomItemIdentifiers.ESCAPE_KEY) {
            player.message("You cannot add the Escape key to your looting bag.");
            return false;
        }

        // Is the player trying to store a looting bag inside a looting bag?
        if (item.getId() == LOOTING_BAG || item.getId() == LOOTING_BAG_22586) {
            player.message("You may be surprised to learn that bagception is not permitted.");
            return false;
        }

        // Is the player inside the wilderness?
        if (!WildernessArea.inWild(player)&& !player.getMemberRights().isEliteMemberOrGreater(player)) {
            player.message("You can't put items in the looting bag unless you're in the Wilderness.");
            return false;
        }

        // Is the item trade-able?
        if (!item.rawtradable()) {
            player.message("Only tradeable items can be put in the bag.");
            return false;
        }

        return depositItemsIntoBag(item.getId(), amount, groundItem);
    }

    public boolean depositItemsIntoBag(int itemID, int amtToStore, GroundItem groundItem) {
        if (player.getLootingBag().isFull() || player.getLootingBag().size() >= SIZE) {
            player.message("You do not have enough space in your looting bag.");
            return false;
        }

        int requestedAmount = amtToStore;
        int maxAmount = groundItem != null ? groundItem.getItem().getAmount() : player.inventory().count(itemID);

        //Does our player have enough of the item to store that they requested?
        if (maxAmount < amtToStore)
            requestedAmount = maxAmount;

        Item item = new Item(itemID, requestedAmount);

        if (groundItem == null) {
            player.inventory().remove(new Item(itemID, requestedAmount), true);
        }

        player.getLootingBag().add(item, true);
        lootingBagLogs.log(LOOTING_BAG_LOGS, "Player " + player.getUsername() + " added: " + item.unnote().name());
        return true;
    }

    /**
     * Removing items from the looting bag
     *
     * @param item The item to withdraw
     * @param slot The slot to remove the item from
     */
    public void withdrawBank(Item item, int slot) {
        //Make sure said item is in the looting bag
        if (!player.getLootingBag().contains(item)) {
            return;
        }

        int amount = count(item.getId());
        if (item.getAmount() > amount) {
            item = item.createWithAmount(amount);
        }
        lootingBagLogs.log(LOOTING_BAG_LOGS, "Player " + player.getUsername() + " withdrawn: " + item.unnote().name());
        player.getBank().depositFromNothing(item);
        player.getLootingBag().remove(item, slot, true);
        player.getBank().refresh();
        onRefresh();
    }

    /**
     * Store an item in the looting bag using the {@code Item}used with the looting
     * bag.
     *
     * @param used The item we want to store
     * @param with Inside the looting bag
     */
    public boolean itemOnItem(Item used, Item with) {
        if (!(used.getId() == LOOTING_BAG || with.getId() == LOOTING_BAG || used.getId() == LOOTING_BAG_22586 || with.getId() == LOOTING_BAG_22586)) {
            return false;
        }

        if (used.getId() == LOOTING_BAG) {
            int count = player.inventory().count(with.getId());
            if (count >= 1) {
                deposit(with, with.getAmount(), null);
                return true;
            }
            depositMenu(with);
            return true;
        }

        if (with.getId() == LOOTING_BAG) {
            if (used.getAmount() == 1) {
                deposit(used, used.getAmount(), null);
                return true;
            }
            depositMenu(used);
            return true;
        }

        if (used.getId() == LOOTING_BAG_22586) {
            int count = player.inventory().count(with.getId());
            if (count >= 1) {
                deposit(with, with.getAmount(), null);
                return true;
            }
            depositMenu(with);
            return true;
        }

        if (with.getId() == LOOTING_BAG_22586) {
            if (used.getAmount() == 1) {
                deposit(used, used.getAmount(), null);
                return true;
            }
            depositMenu(used);
            return true;
        }
        return false;
    }

    public static int OPEN_LOOTING_BAG = LOOTING_BAG_22586;

    public boolean lootbagOpen() {
        return (player.inventory().contains(OPEN_LOOTING_BAG));
    }

    /**
     * Handles depositing the entire looting bag.
     */
    public void depositLootingBag() {
        for (int i = 0; i <= 27; i++) {
            var itemAt = player.getLootingBag().get(i);
            if (itemAt == null) continue; // Get item or continue
            player.getBank().deposit(i, itemAt.getAmount(), player.getLootingBag());
        }
        player.getPacketSender().sendString(BANK_EMPTY_STRING, "The bag is empty.");
    }

    @Override
    public void onRefresh() {
        if (player.getLootingBag().isEmpty()) {
            player.getPacketSender().clearItemOnInterface(InterfaceConstants.LOOTING_BAG_OPEN_CONTAINER_ID);
            player.getPacketSender().sendString(EMPTY_STRING, "The bag is empty.");
        }

        if (depositing) {
            player.getPacketSender().sendItemOnInterface(InterfaceConstants.LOOTING_BAG_DEPOSIT_CONTAINER_ID, player.inventory().toArray());
            player.getPacketSender().sendString(BAG_VALUE_STRING, "Bag value: " + Utils.formatNumber(containerValue()) + " bm");
            return;
        }
        if (bankStoring) {
            player.getPacketSender().sendItemOnInterface(InterfaceConstants.LOOTING_BAG_BANK_CONTAINER_ID, toArray());
            if (this.isEmpty()) {
                player.getPacketSender().sendString(BANK_EMPTY_STRING, "The bag is empty.");
            }
            player.getPacketSender().sendString(BANK_EMPTY_STRING, "");
            return;
        }
        player.getPacketSender().sendString(EMPTY_STRING, "");
        player.getPacketSender().sendString(VALUE_STRING, "Value: " + Utils.formatNumber(containerValue()) + " bm");
        player.getPacketSender().sendItemOnInterface(InterfaceConstants.LOOTING_BAG_OPEN_CONTAINER_ID, player.getLootingBag().toArray());
    }

    /**
     * An {@link ItemContainerAdapter} implementation that listens for changes to
     * the looting bag.
     */
    private static final class LootingBagListener extends ItemContainerAdapter {

        /**
         * Creates a new {@link LootingBag.LootingBagListener}.
         */
        LootingBagListener(Player player) {
            super(player);
        }

        @Override
        public int getWidgetId() {
            return InterfaceConstants.LOOTING_BAG_DEPOSIT_CONTAINER_ID;
        }

        @Override
        public String getCapacityExceededMsg() {
            return "You do not have enough space in your looting bag.";
        }
    }

}
