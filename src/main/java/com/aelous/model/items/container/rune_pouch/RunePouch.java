package com.aelous.model.items.container.rune_pouch;

import com.aelous.model.entity.player.InputScript;
import com.aelous.utility.ItemIdentifiers;
import com.google.common.collect.ImmutableSet;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.aelous.model.entity.attributes.AttributeKey.VIEWING_RUNE_POUCH_I;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * This class handles the functionality of the rune pouch
 * 
 * @author Patrick van Elderen | 12 mrt. 2019 : 15:13:40
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class RunePouch extends ItemContainer {

    private static final Logger logger = LogManager.getLogger(RunePouch.class);

    /**
     * The rune pouch runes container
     */
    private static final int RUNE_POUCH_CONTAINER = 48705;

    /**
     * The inventory container
     */
    private static final int RUNE_POUCH_INVENTORY_CONTAINER = 48706;

    private static final int CLOSE_BUTTON = 48702;

    /**
     * Maximum amount of runes we can store
     */
    private static final int MAXIMUM_RUNE_CAPACITY = 16_000;

    /**
     * runes allowed to be inside the rune pouch
     */
    private static final ImmutableSet<Integer> RUNES = ImmutableSet.of(FIRE_RUNE, WATER_RUNE, AIR_RUNE, EARTH_RUNE, MIND_RUNE, BODY_RUNE, DEATH_RUNE, NATURE_RUNE, CHAOS_RUNE, LAW_RUNE, COSMIC_RUNE, BLOOD_RUNE, SOUL_RUNE, ASTRAL_RUNE, 4698, STEAM_RUNE, MIST_RUNE, DUST_RUNE, SMOKE_RUNE, MUD_RUNE, LAVA_RUNE, WRATH_RUNE);

    /** The player instance. */
    private final Player player;

    /** Constructs a new <code>RunePouch</code>. */
    public RunePouch(Player player) {
        super(4, StackPolicy.ALWAYS);
        this.player = player;
    }

    public boolean quickFill(int id) {
        if (id != RUNE_POUCH) {
            return false;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Barrage", "Teleblock", "Vengeance");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                //Barrage
                if(option == 1) {
                    if(!player.getRunePouch().isEmpty()) {
                        player.message(Color.RED.wrap("Empty your pouch before adding a preset. This could overwrite your runes."));
                        stop();
                        return;
                    }
                    if (player.getRunePouch().isFull()) {
                        player.getPacketSender().sendMessage("Your rune pouch is already full!");
                        stop();
                        return;
                    }
                    if (player.getInventory().containsAll(DEATH_RUNE, BLOOD_RUNE, WATER_RUNE)) {
                        player.getRunePouch().deposit(new Item(DEATH_RUNE, player.getInventory().byId(DEATH_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(BLOOD_RUNE, player.getInventory().byId(BLOOD_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(WATER_RUNE, player.getInventory().byId(WATER_RUNE).getAmount()));
                        player.getInterfaceManager().closeDialogue();

                        player.getRunePouch().refresh(); //TODO

                        stop();

                    } else {
                        player.getInterfaceManager().closeDialogue();
                        player.message(Color.RED.wrap("You need at least 3 death runes, 3 blood runes and 3 water runes to fill the pouch."));
                    }
                } else if(option == 2) {
                    if(!player.getRunePouch().isEmpty()) {
                        player.message(Color.RED.wrap("Empty your pouch before adding a preset. This could overwrite your runes."));
                        stop();
                        return;
                    }
                    if (player.getInventory().containsAll(LAW_RUNE, CHAOS_RUNE, DEATH_RUNE)) {
                        player.getRunePouch().deposit(new Item(LAW_RUNE, player.getInventory().byId(LAW_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(CHAOS_RUNE, player.getInventory().byId(CHAOS_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(DEATH_RUNE, player.getInventory().byId(DEATH_RUNE).getAmount()));
                        player.getInterfaceManager().closeDialogue();
                        player.getRunePouch().refresh();
                        stop();
                    } else {
                        player.getInterfaceManager().closeDialogue();
                        player.message(Color.RED.wrap("You need at least 3 Law runes, 3 Chaos runes and 3 Water runes to fill the pouch."));
                    }
                } else if(option == 3) {
                    if(!player.getRunePouch().isEmpty()) {
                        player.message(Color.RED.wrap("Empty your pouch before adding a preset. This could overwrite your runes."));
                        stop();
                        return;
                    }
                    if (player.getInventory().containsAll(ASTRAL_RUNE, DEATH_RUNE, EARTH_RUNE)) {
                        player.getRunePouch().deposit(new Item(ASTRAL_RUNE,player.getInventory().byId(ASTRAL_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(DEATH_RUNE,player.getInventory().byId(DEATH_RUNE).getAmount()));
                        player.getRunePouch().deposit(new Item(EARTH_RUNE,player.getInventory().byId(EARTH_RUNE).getAmount()));
                        player.getInterfaceManager().closeDialogue();
                        player.getRunePouch().refresh();
                    stop();
                    } else {
                        player.getInterfaceManager().closeDialogue();
                        player.message(Color.RED.wrap("You need at least 3 Death runes, 3 Astral runes and 3 Earth runes to fill the pouch."));
                    }
                }
            }
        });
        return true;
    }

    public boolean onButton(int button) {
        if(button == CLOSE_BUTTON) {
            player.getInterfaceManager().close();
            player.clearAttrib(VIEWING_RUNE_POUCH_I);
        }
        return false;
    }

    public boolean removeFromPouch(int interfaceId, int id, int slot, int type) {
        if (interfaceId != RUNE_POUCH_CONTAINER) {
            return false;
        }
        Item item = new Item(id);

        if(type == 1) {
            player.getRunePouch().withdraw(item.getId(), 1);
        } else if(type == 2) {
            player.getRunePouch().withdraw(item.getId(), 10);
        } else if(type == 3) {
            player.getRunePouch().withdraw(item.getId(), 100);
        } else if(type == 4) {
            if (player.getRunePouch().containsId(item.getId())) {
                item.setAmount(player.getRunePouch().getRuneAmount(item.getId()));
            }
            player.getRunePouch().withdraw(item.getId(), item.getAmount());
        } else if(type == 5) {

            player.setAmountScript("How many would you like to withdraw?", value -> {
                int amount = (Integer) value;
                if (id < 0 || slot < 0 || amount <= 0)
                    return false;

                player.getRunePouch().withdraw(id, amount);
                return true;
            });
        }
        return true;
    }

    public boolean moveToRunePouch(int interfaceId, int id, int slot, int type) {
        if (interfaceId != RUNE_POUCH_INVENTORY_CONTAINER) {
            return false;
        }

        if(type == 1) {
            player.getRunePouch().deposit(new Item(id, 1));
        } else if(type == 2) {
            player.getRunePouch().deposit(new Item(id, 10));
        } else if(type == 3) {
            player.getRunePouch().deposit(new Item(id, 100));
        } else if(type == 4) {
            player.getRunePouch().deposit(new Item(id, player.inventory().count(id)));
        } else if(type == 5) {
            player.setAmountScript("How many would you like to deposit?", value -> {
                int amount = (Integer) value;
                if (id < 0 || slot < 0 || amount <= 0)
                    return false;
                player.getRunePouch().deposit(new Item(id, (int) amount));
                return true;
            });
        }
        return true;
    }

    public boolean open(int id) {
        //Safety check
        if (id != RUNE_POUCH) {
            return false;
        }

        if (size() > 3) {
            Item fourthSlot = player.getRunePouch().get(3);
            player.getRunePouch().remove(fourthSlot);
            player.inventory().addOrBank(fourthSlot);
        }

        player.getInterfaceManager().open(48700);
        player.getInterfaceManager().setSidebar(3, -1);
        refresh();
        return true;
    }

    public void close() {
        player.getInterfaceManager().setSidebar(3, 3213);
        player.clearAttrib(VIEWING_RUNE_POUCH_I);
    }
    
    public void sync() {
        // The rune pouch item container
        player.getPacketSender().sendItemOnInterface(48705, player.getRunePouch().toArray());
        //Update the inventory container
        player.getPacketSender().sendItemOnInterface(48706, player.inventory().getItems());

        // Custom method sending the runeIds to the client
        sendCounts(player);
    }

    /**
     * Removes the rune from the container by the specified
     * {@code amount}. And adds it back in the inventory.
     *
     * @param rune     the rune that is being stored.
     * @param amount the amount that is being withdrawn.
     */
    public void withdraw(int rune, int amount) {
        Item item = new Item(rune, amount);
        amount = count(item.getId());
        if (item.getAmount() > amount) {
            item = item.createWithAmount(amount);
        }
        player.getRunePouch().remove(item);
        player.inventory().add(item);
        refresh();
    }

    /**
     * Attempts to store an rune to the container by the specified
     * {@code amount}.
     *
     * @param item
     *            the rune that is being stored.
     * @return {@code true} if an item is stored, {@code false} otherwise.
     */
    public void deposit(Item item) {
        boolean canAdd = RUNES.stream().anyMatch(rune -> rune == item.getId());

        if (!canAdd) {
            player.message("Don't be silly.");
            return;
        }

        int amount = item.getAmount();
        int runeAmount = getRuneAmount(item.getId());
        if (runeAmount >= MAXIMUM_RUNE_CAPACITY) {
            player.message("You can only have a total of " + Utils.format(MAXIMUM_RUNE_CAPACITY) + " runes in your rune pouch.");
            return;
        }

        if (amount > item.getAmount()) {
            amount = item.getAmount();
        }

        if (MAXIMUM_RUNE_CAPACITY - runeAmount < amount) {
            amount = MAXIMUM_RUNE_CAPACITY - runeAmount;
        }

        for (Item rune : player.getRunePouch().toNonNullArray()) {
            if (item.getId() == rune.getId()) {
                player.inventory().remove(item.getId(), amount);
                rune.incrementAmountBy(amount);
                refresh();
                return;
            }
        }

        player.inventory().remove(item.getId(), amount);
        player.getRunePouch().add(new Item(item.getId(), amount));
        refresh();
    }

    public boolean itemOnItem(Item used, Item with) {
        if (!(used.getId() == RUNE_POUCH || with.getId() == RUNE_POUCH)) {
            return false;
        }

        if (used.getId() == RUNE_POUCH) {
            if (with.getAmount() >= 1) {
                deposit(with);
                refresh();
                return true;
            }
            return true;
        }

        if (with.getId() == RUNE_POUCH) {
            if (used.getAmount() >= 1) {
                deposit(used);
                refresh();
                return true;
            }
            return true;
        }
        return false;
    }

    public int getRuneAmount(int id) {
        int amount = 0;
        for (Item rune : player.getRunePouch().toArray()) {
            if (rune != null && rune.getId() == id)
                amount += rune.getAmount();
        }
        return amount;
    }

    public boolean containsId(int item) {
        for (Item rune : player.getRunePouch().toArray()) {
            if (rune != null && rune.getId() == item)
                return true;
        }
        return false;
    }

    /**
     * Drops all items on death
     */
    public Item[] dropItemsOnDeath() {
        return toNonNullArray();
    }

    /**
     * Sends the runeIds to the client in form of a string. This will make the
     * spells light up.
     *
     * @param sendToClient
     *            The player that sends the string to the client.
     */
    private void sendCounts(Player sendToClient) {
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        Item i1;
        Item i2;
        Item i3;
        Item i4;
        i1 = (sendToClient.getRunePouch().player.getRunePouch().size() > 0) ? sendToClient.getRunePouch().get(0) : null;
        i2 = (sendToClient.getRunePouch().player.getRunePouch().size() > 1) ? sendToClient.getRunePouch().get(1) : null;
        i3 = (sendToClient.getRunePouch().player.getRunePouch().size() > 2) ? sendToClient.getRunePouch().get(2) : null;
        i4 = (sendToClient.getRunePouch().player.getRunePouch().size() > 3) ? sendToClient.getRunePouch().get(3) : null;
        if(!sendToClient.inventory().contains(RUNE_POUCH)) {
            i1 = null;
            i2 = null;
            i3 = null;
        }
        sb.append(i1 == null ? "0" : "" + i1.getId());
        sb.append(":");
        sb.append(i1 == null ? "0" : "" + i1.getAmount());
        sb.append("-");
        sb.append(i2 == null ? "0" : "" + i2.getId());
        sb.append(":");
        sb.append(i2 == null ? "0" : "" + i2.getAmount());
        sb.append("-");
        sb.append(i3 == null ? "0" : "" + i3.getId());
        sb.append(":");
        sb.append(i3 == null ? "0" : "" + i3.getAmount());
        sb.append("-");
        sb.append(i4 == null ? "0" : "" + i4.getId());
        sb.append(":");
        sb.append(i4 == null ? "0" : "" + i4.getAmount());
        sb.append("$");

        sendToClient.getPacketSender().sendString(49999, sb.toString());
        //logger.trace("send rune pouch amts {}", sb.toString());
    }

    public void bankRunesFromNothing() {
        //logger.trace("Player {} banking rp runes {} {}", player.getMobName(), player.getRunePouch().size(), Arrays.toString(player.getRunePouch().toArray()));
        for (int index = 0; index < player.getRunePouch().size(); index++) {
            if (player.getRunePouch().get(index) == null) continue;
            player.getBank().depositFromNothing(player.getRunePouch().get(index).copy());
            player.getRunePouch().clear();
            refresh();
        }
    }

    public void empty() {
        if(player.getRunePouch().isEmpty()) {
            player.message("There are no runes in your rune pouch.");
            return;
        }
        for (int index = 0; index < player.getRunePouch().size(); index++) {
            if (player.getRunePouch().get(index) == null)
                continue;
            player.inventory().addOrBank(player.getRunePouch().toArray());
            player.getRunePouch().clear();
            refresh();
        }
    }
}
