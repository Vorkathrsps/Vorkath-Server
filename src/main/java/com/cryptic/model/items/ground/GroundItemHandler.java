package com.cryptic.model.items.ground;

import com.cryptic.model.content.duel.Dueling;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.lootkeys.LootKey;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem.State;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.cryptic.utility.CustomItemIdentifiers.*;

/**
 * A handler for a collection of {@link GroundItem}s
 *
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class GroundItemHandler {

    private static final Logger pickupLogs = LogManager.getLogger("PickupLogs");
    private static final Level PICKUPS;

    static {
        PICKUPS = Level.getLevel("PICKUPS");
    }

    private static final Logger logger = LogManager.getLogger(GroundItemHandler.class);

    /**
     * A list containing all of the ground items
     */
    @Getter
    private static final List<GroundItem> groundItems = new ArrayList<>();

    /**
     * Checks if the ground item is actually on the clicked location.
     *
     * @param id The ground item being checked
     */
    public static Optional<GroundItem> getGroundItem(int id, Tile tile, Player owner) {
        return groundItems
            .stream()
            .filter(item ->
                item.getItem().getId() == id
                    && item.getTile().getX() == tile.getX()
                    && item.getTile().getY() == tile.getY()
                    &&
                    (item.getState() == State.SEEN_BY_EVERYONE
                        || item.getOwnerHash() == -1
                        || owner.getLongUsername() == item.getOwnerHash())).findFirst();
    }

    /**
     * Sends a remove ground item packet to all players.
     *
     * @param groundItem the ground item
     */
    public static boolean sendRemoveGroundItem(GroundItem groundItem) {
        if (!groundItem.isRemoved()) {
            groundItem.setRemoved(true);
            if (groundItem.getState() == State.SEEN_BY_EVERYONE) {
                removeRegionalItem(groundItem);
            } else {
                World.getWorld().getPlayers().stream().filter(p -> Objects.nonNull(p) && p.getLongUsername() == groundItem.getOwnerHash()
                        && p.tile().distance(groundItem.getTile()) <= 64)
                    .forEach(p -> p.getPacketSender().deleteGroundItem(groundItem));
            }
            return true;
        }
        return false;
    }

    /**
     * Removes all ground items when the player leaves the region
     *
     * @param groundItem the ground item
     */
    private static void removeRegionalItem(GroundItem groundItem) {
        for (Player player : World.getWorld().getPlayers()) {
            if (player == null || player.distanceToPoint(groundItem.getTile().getX(), groundItem.getTile().getY()) > 64) {
                continue;
            }

            //We can go ahead and send the remove ground item packet
            player.getPacketSender().deleteGroundItem(groundItem);
        }
    }

    /**
     * Add ground items for players when entering region.
     *
     * @param groundItem The ground item
     */
    private static void addRegionalItem(GroundItem groundItem) {
        for (Player player : World.getWorld().getPlayers()) {
            if (player == null) {
                continue;
            }
            if (player.tile().getLevel() != groundItem.getTile().getLevel() || player.distanceToPoint(groundItem.getTile().getX(), groundItem.getTile().getY()) > 64) {
                continue;
            }

            if (player.getUsername().equalsIgnoreCase("Box test")) {
                continue;
            }

            // If we are globalizing an item, don't re-add it for the owner
            if (player.getLongUsername() != groundItem.getOwnerHash()) {

                //Don't add private items to the region yet, we only add public items
                if (groundItem.getState() == State.SEEN_BY_OWNER) {
                    continue;
                }

                Item item = new Item(groundItem.getItem().getId());

                //If the item is a non-tradable item continue
                if (!item.rawtradable()) {
                    continue;
                }

                //Checks if we're able to view the ground item
                if (player.distanceToPoint(groundItem.getTile().getX(), groundItem.getTile().getY()) <= 60 && player.tile().getLevel() == player.tile().getLevel()) {
                    player.getPacketSender().createGroundItem(groundItem);
                }
            }
        }
    }

    /**
     * The ground item task, removes the ticks
     */
    public static void pulse() {
        Iterator<GroundItem> iterator = groundItems.iterator();
        while (iterator.hasNext()) {
            GroundItem item = iterator.next();

            if (item == null) continue;

            if (item.isRemoved()) {
                iterator.remove();
                continue;
            }

            if (item.decreaseTimer() < 1) {
                if (item.getState() == State.SEEN_BY_EVERYONE) {
                    item.setRemoved(true);
                    iterator.remove();
                    removeRegionalItem(item);
                }

                if (item.getState() == State.SEEN_BY_OWNER) {
                    item.setState(State.SEEN_BY_EVERYONE);
                    item.setTimer(200);
                    addRegionalItem(item);
                }
            }
        }
    }

    /**
     * The method that updates all items in the region for {@code player}.
     *
     * @param player the player to update items for.
     */
    public static void updateRegionItems(Player player) {
        for (GroundItem item : groundItems) {
            player.getPacketSender().deleteGroundItem(item);
        }
        for (GroundItem item : groundItems) {

            if (player.tile().getLevel() != item.getTile().getLevel() || player.distanceToPoint(item.getTile().getX(), item.getTile().getY()) > 60) {
                continue;
            }

            Item items = new Item(item.getItem().getId());

            if (items.rawtradable() || item.getOwnerHash() == player.getLongUsername()) {

                if (item.getState() == State.SEEN_BY_EVERYONE || item.getOwnerHash() == player.getLongUsername()) {
                    //System.out.println(String.format("spawned: %s%n", item));
                    player.getPacketSender().createGroundItem(item);
                }
            }
        }
    }

    public static boolean createGroundItem(GroundItem item) {
        Player player = item.getPlayer();
        if (item.getItem().getId() < 0) {
            return false;
        }

        if (player == null) {
            item.setState(State.SEEN_BY_EVERYONE);
        }

        // Stackable? Can group with existing of the same item on that tile
        if (item.getItem().stackable()) {
            for (GroundItem other : groundItems) {
                // Same id, location, still valid
                if (item.getItem().getId() == other.getItem().getId()
                    && item.getTile().getX() == other.getTile().getX()
                    && item.getTile().getY() == other.getTile().getY()
                    && item.getTile().getLevel() == other.getTile().getLevel() && !other.isRemoved()) {

                    // Global or seen by all
                    if (other.getState() == State.SEEN_BY_EVERYONE || (other.getOwnerHash() != -1 && other.getOwnerHash() == item.getOwnerHash())) {

                        // Amount of that item.
                        long existing = other.getItem().getAmount();

                        // If added together total is less than int overload.
                        if (existing + item.getItem().getAmount() <= Integer.MAX_VALUE) {

                            final int oldAmt = other.getItem().getAmount();
                            // Update amount
                            other.getItem().setAmount((int) (existing + item.getItem().getAmount()));

                            // Reset expiry timer, same as dropping a new item.
                            other.setTimer(item.getState() == State.SEEN_BY_EVERYONE ? 200 : 100);

                            if (other.getState() == State.SEEN_BY_EVERYONE) {
                                for (Player p2 : World.getWorld().getPlayers()) {
                                    if (p2 == null || p2.distanceToPoint(other.getTile().getX(), other.getTile().getY()) > 64) {
                                        continue;
                                    }
                                    p2.getPacketSender().updateGroundItemAmount(oldAmt, other);
                                }
                            } else {
                                if (player != null && player.distanceToPoint(other.getTile().getX(), other.getTile().getY()) <= 64) {
                                    player.getPacketSender().updateGroundItemAmount(oldAmt, other);
                                }
                            }

                            // Return true for entire method. No need to re-send items.
                            //logger.info("INFO: item "+item.getItem().getId()+" stacks with existing "+other.getItem().getId());
                            return true;
                        }
                    }
                }
            }
        }

        groundItems.add(item);
        // disappear in 2 mins if starts as global, otherwise 1 minute until it'll go from private to global
        item.setTimer(item.getState() == State.SEEN_BY_EVERYONE ? 200 : 100);
        if (player != null) {
            player.getPacketSender().createGroundItem(item);
        }

        if (item.getState() == State.SEEN_BY_EVERYONE) {
            addRegionalItem(item);
        }

        return true;
    }

    public static String[] flagged_items = new String[]{
        "elysian_spirit_shield", "torva", "zaryte", "virtus", "spectral_spirit_shield",
        "arcane_spirit_shield", "tumeken", "masori", "bandos", "armadyl", "blowpipe", "webweaver",
        "viggora", "craws_bow", "ursine", "toxic_staff", "ancestral", "twisted_bow", "dragon_claws",
        "voidwaker", "ancient_godsword", "bow_of_faerdhinen", "anguish", "torture", "tormented",
        "zenyte"};

    public static void pickup(Player player, int id, Tile tile) {
        Optional<GroundItem> optionalGroundItem = getGroundItem(id, tile, player);
        if (optionalGroundItem.isPresent()) {

            player.action.clearNonWalkableActions();

            GroundItem groundItem = optionalGroundItem.get();

            var different_owner = !groundItem.ownerMatches(player);
            var groundItemPked = groundItem.pkedFrom() != null && groundItem.pkedFrom().equalsIgnoreCase(player.getUsername());

            //System.out.println("different_owner "+different_owner+" groundItemPked "+groundItemPked);

            // Ironman checks (if it's not a respawning item)
            if (player.getIronManStatus() != IronMode.NONE) {
                if (different_owner && !groundItemPked) { // Owner different? It could be pked!
                    player.message("You're an Iron Man, so you can't take items that other players have dropped.");
                    return;
                } else if (groundItem.pkedFrom() != null && !groundItem.pkedFrom().equalsIgnoreCase(player.getUsername())) {
                    player.message("You're an Iron Man, so you can't take items that other players have dropped.");
                    return;
                }
            } else if (player.getGameMode().isIronman()) {
                if (different_owner && !groundItemPked) { // Owner different? It could be pked!
                    player.message("You're an Dark Lord, so you can't take items that other players have dropped.");
                    return;
                } else if (groundItem.pkedFrom() != null && !groundItem.pkedFrom().equalsIgnoreCase(player.getUsername())) {
                    player.message("You're an Dark Lord, so you can't take items that other players have dropped.");
                    return;
                }
            }

            if (different_owner && Dueling.in_duel(player)) {
                player.message("You can't pickup other players items in the duel arena.");
                return;
            }

            Chain.noCtx().runFn(1, () -> {
                if (groundItem.getState() != State.SEEN_BY_EVERYONE && groundItem.getOwnerHash() != player.getLongUsername()) {
                    return;
                }

                if (groundItem.isRemoved()) {
                    return;
                }

                Item item = groundItem.getItem();

                boolean lootingBagOpened = player.getLootingBag().lootbagOpen();
                if (player.tile().isWithinDistance(groundItem.getTile(), 1)) {
                    if (lootingBagOpened && player.getLootingBag().deposit(item, item.getAmount(), groundItem)) {
                        sendRemoveGroundItem(groundItem);
                        return;
                    }

                    // If we've made it here then it added to the inventory.
                    if (player.inventory().getFreeSlots() == 0 && !(player.inventory().contains(item.getId()) && item.stackable())) {
                        player.message("You don't have enough inventory space to hold that item.");
                        return;
                    } else {
                        if (item.getId() == ESCAPE_KEY) {
                            if (WildernessArea.isInWilderness(player)) {
                                player.confirmDialogue(new String[]{Color.RED.wrap("Are you sure you wish to pick up this key? You will be red"), Color.RED.wrap("skulled and all your items will be lost on death!")}, "", "Proceed.", "Cancel.", () -> {
                                    Optional<GroundItem> gItem = GroundItemHandler.getGroundItem(ESCAPE_KEY, tile, player);
                                    if (gItem.isEmpty()) {
                                        return;
                                    }
                                    Skulling.assignSkullState(player, SkullType.RED_SKULL);
                                    player.inventory().add(item);
                                    sendRemoveGroundItem(groundItem);
                                    player.getRisk().update();
                                    pickupLogs.log(PICKUPS, "[Player: " + player.getUsername() + "] [Looted Item: (" + item.getAmount() + "x) - (" + item.unnote().name() + ") - (id " + item.getId() + ")]");
                                    Utils.sendDiscordInfoLog("[Looted Item: (" + item.getAmount() + "x) - (" + item.unnote().name() + ") - (id " + item.getId() + ")]", "pickups");
                                    player.inventory().refresh();
                                });
                            }
                            return;
                        }
                    }
                    {

                        boolean added = player.inventory().add(item);
                        if (!added) {
                            player.message("There is not enough space in your inventory to hold any more items.");
                            return;
                        }
                    }

                    if (LootKey.KEYS.stream().anyMatch(key -> item.getId() == key)) {
                        LootKey.pickupKey(player, groundItem);
                    }

                    // If we've made it here then it added to the inventory.
                    sendRemoveGroundItem(groundItem);
                    player.getRisk().update();
                    //pickupLogs.log(PICKUPS, "Player " + player.getUsername() + " picked up item " + item.getAmount() + "x " + item.unnote().name() + " (id " + item.getId() + ") at X: " + groundItem.getTile().x + " Y: " + groundItem.getTile().y);
                    Utils.sendDiscordInfoLog("[Player: " + player.getUsername() + "] - ```[Looted Item: (" + item.getAmount() + "x) - (" + item.unnote().name() + ") - (id " + item.getId() + ") - [Tile (X: " + groundItem.getTile().x + ", Y: " + groundItem.getTile().y + ")]```", "pickups");

                    var val = item.getValue() * item.getAmount();
                    String severity = val >= 1_000_000_000 || val <= -1 ? "High" : "Low";

                    if (item.getAmount() >= 10) {
                        for (String flaggedItem : flagged_items) {
                            if (!item.unnote().name().toLowerCase().contains(flaggedItem)) continue;
                            Utils.sendDiscordInfoLog(
                                "``` [Severity: " + severity + "] " +
                                    " - [Player: " + player.getUsername() + "]" +
                                    " - [Item Name: " + item.unnote().name() + "]" +
                                    " - [Item Amount: " + Utils.formatRunescapeStyle(item.getAmount()) + "]" +
                                    " - [Item Value: " + Utils.formatRunescapeStyle(val) + "]" +
                                    " - [Item Id: " + item.getId() + "]```", "dupe");
                        }
                    }

                    Utils.sendDiscordInfoLog(
                        "``` [Severity: " + severity + "] " +
                            " - [Player: " + player.getUsername() + "]" +
                            " - [Item Name: " + item.unnote().name() + "]" +
                            " - [Item Amount: " + Utils.formatRunescapeStyle(item.getAmount()) + "]" +
                            " - [Item Value: " + Utils.formatRunescapeStyle(val) + "]" +
                            " - [Item Id: " + item.getId() + "]```", "dupe");

                    player.getInventory().refresh();

                    if (groundItem.respawns()) {
                        Chain.runGlobal(groundItem.respawnTimer(), () -> {
                            GroundItem itemToSpawn = new GroundItem(item, groundItem.getTile().copy(), null);
                            itemToSpawn.respawns(true);
                            GroundItemHandler.createGroundItem(itemToSpawn);
                        });
                    }
                }
            });
        }
    }
}
