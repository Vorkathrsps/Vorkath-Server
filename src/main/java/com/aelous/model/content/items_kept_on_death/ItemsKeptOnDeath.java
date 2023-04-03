package com.aelous.model.content.items_kept_on_death;

import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;

import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.test.unit.IKODTest;
import com.aelous.utility.ItemIdentifiers;

import java.util.*;
import java.util.stream.Collectors;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Handles items kept on death.
 * @author Swiffy
 */
public class ItemsKeptOnDeath {

   private static List<Item> itemsKeptOnDeath = new ArrayList<>();
   private static List<Item> itemsAlwaysKeptOnDeath = new ArrayList<>();
   private static List<Item> itemsLostOnDeath = new ArrayList<>();
   private static long lostItemsValue = -1;

    /**
     * Sends the items kept on death interface for a player.
     * @param player    Player to send the items kept on death interface for.
     */
    public static void open(Player player) {
        sendInterfaceData(player);
        player.getInterfaceManager().open(17100);
    }

    /**
     * Sends the items kept on death data for a player.
     * @param player    Player to send the items kept on death data for.
     */
    public static void sendInterfaceData(Player player) {
        clearAndRecalc(player);
        // 2ndly need to heavily optimize risk calc

        // no need to send all these when inter isnt even open
        player.getPacketSender().sendItemOnInterface(17109, itemsKeptOnDeath.toArray(new Item[0]));
        player.getPacketSender().sendItemOnInterface(17110, itemsAlwaysKeptOnDeath.toArray(new Item[0]));
        player.getPacketSender().sendItemOnInterface(17111, itemsLostOnDeath.toArray(new Item[0]));
    }

    public static void clearAndRecalc(Player player) {
        itemsKeptOnDeath.clear();
        itemsAlwaysKeptOnDeath.clear();
        itemsLostOnDeath.clear();
        lostItemsValue = 0;
        calculateItems(player);
    }

    /**
     * attempts should be made to match and consolidate the logic into {@link com.aelous.utility.test.unit.PlayerDeathDropResult}
     * @param player
     */
    public static void calculateItems(Player player) {

        List<Item> tempToDrop = new LinkedList<>();
        // inv+equip+bag+rp is max like 70 items this calc shouldnt be too heavy

        player.getEquipment().forEach(tempToDrop::add);

        Item[] lootingBag = player.getLootingBag().dropItemsOnDeath();
        Item[] runePouch = player.getRunePouch().dropItemsOnDeath();
        player.inventory().forEach(item -> {
            if (!item.matchesId(RUNE_POUCH) && !item.matchesId(LOOTING_BAG) && !item.matchesId(LOOTING_BAG_22586)) {
                tempToDrop.add(item);
            } else {
                itemsLostOnDeath.add(item);
            }
        });

        if (lootingBag != null) {
            tempToDrop.addAll(Arrays.asList(lootingBag));
        }

        if (runePouch != null) {
            tempToDrop.addAll(Arrays.asList(runePouch));
        }

        LinkedList<Item> toDrop = new LinkedList<>(tempToDrop);

        // remove always kept before calculating kept-3 by value
        List<Item> alwaysKept = toDrop.stream().filter(ItemsKeptOnDeath::alwaysKept).collect(Collectors.toList());
        itemsAlwaysKeptOnDeath.addAll(alwaysKept); // so static list is for the visual interface but for actually ikod idk why this list isn't also used
        toDrop.removeIf(ItemsKeptOnDeath::alwaysKept);

        // Sort remaining lost items by value.
        boolean sort = true;
        if (sort) {
            toDrop.sort((o1, o2) -> {
                o1 = o1.unnote();
                o2 = o2.unnote();

                ItemDefinition def = o1.definition(World.getWorld());
                ItemDefinition def2 = o2.definition(World.getWorld());

                int v1 = 0;
                int v2 = 0;

                if (def != null) {
                    v1 = o1.getValue();
                    if (v1 <= 0 && !GameServer.properties().pvpMode) {
                        v1 = o1.getBloodMoneyPrice().value();
                    }
                }
                if (def2 != null) {
                    v2 = o2.getValue();
                    if (v2 <= 0 && !GameServer.properties().pvpMode) {
                        v2 = o2.getBloodMoneyPrice().value();
                    }
                }

                return Integer.compare(v2, v1);
            });
        }

        int itemsKept = (Skulling.skulled(player) ? 0 : 3);

        // On Ultimate Iron Man, you drop everything!
        if (player.getIronManStatus() == IronMode.ULTIMATE) {
            itemsKept = 0;
        }

        boolean protection_prayer = Prayers.usingPrayer(player, Prayers.PROTECT_ITEM);

        if (protection_prayer) {
            itemsKept++;
        }

        if (player.getSkullType().equals(SkullType.RED_SKULL) || player.getGameMode().isDarklord()) {
            itemsKept = 0;
        }

        IKODTest.debug("kept "+itemsKept+" "+ Skulling.skulled(player));

        while (itemsKept-- > 0 && !toDrop.isEmpty()) {
            Item head = toDrop.peek();
            if (head == null) {
                itemsKept++;
                toDrop.poll();
                continue;
            }

            itemsKeptOnDeath.add(new Item(head.getId(), 1));

            if (head.getAmount() == 1) { // Amount 1? Remove the item entirely.
                toDrop.poll();
            } else { // Amount more? Subtract one amount.
                int index = toDrop.indexOf(head);
                toDrop.set(index, new Item(head, head.getAmount() - 1));
            }
        }

        boolean fcheck = true;
        if (fcheck)
        toDrop.forEach(item -> {

            boolean always_kept = item.definition(World.getWorld()).autoKeptOnDeath;
            // 6 items *

            //System.out.println("running the pvp check");
            if ((always_kept) && !changes(item)) {
                //System.out.println("Autokeep");
                //QOL OSRS doesn't drop them anymore but spawns in inventory.
                itemsAlwaysKeptOnDeath.add(item);
                return;
            }

            //Drop item
            lostItemsValue += (1L * item.getValue() * item.getAmount());
            itemsLostOnDeath.add(item);
        });

        if (IKODTest.IKOD_DEBUG) {
            IKODTest.debug("lost: " + Arrays.toString(itemsLostOnDeath.stream().filter(Objects::nonNull).map(i -> i.toShortString() + " (" + i.getAmount() * i.getValue() + (i.getAmount() > 1 ? "@" + i.getValue() : "") + ")").toArray()));
            IKODTest.debug("kept: " + Arrays.toString(itemsKeptOnDeath.stream().filter(Objects::nonNull).map(i -> i.toShortString() + " (" + i.getAmount() * i.getValue() + (i.getAmount() > 1 ? "@" + i.getValue() : "") + ")").toArray()));
            IKODTest.debug("always kept: " + Arrays.toString(itemsAlwaysKeptOnDeath.stream().filter(Objects::nonNull).map(i -> i.toShortString() + " (" + i.getAmount() * i.getValue() + (i.getAmount() > 1 ? "@" + i.getValue() : "") + ")").toArray()));
            //System.out.println("always kept: "+ Arrays.toString(itemsAlwaysKeptOnDeath.stream().filter(Objects::nonNull).map(i -> i.toShortString()+" ("+i.getAmount()*i.getValue()+(i.getAmount()>1?"@"+i.getValue():"")+")").toArray()));
        }
    }

    public static boolean alwaysKept(Item head) {
        // special case here for tradable specific items:
        if (head.definition(World.getWorld()).tradeable_special_items) {
            return true;
        }
        if (head.definition(World.getWorld()).autoKeptOnDeath &&
            (head.getId() != ItemIdentifiers.SARAS_BLESSED_SWORD_FULL || head.getId() != ItemIdentifiers.SARADOMINS_BLESSED_SWORD)) {
            // items like artifacts that convert to tier-1 are not kept
            if (changes(head)) {
                return false;
            }
            // autokept if untradable
            return !head.makeItemsTradeable();
        }
        return false;
    }

    /**
     * anything that isn't always kept, stuff that converts, zulrah, things with charges, etc
     * mimics {@link com.aelous.core.task.impl.PlayerDeathTask#dropItems(Player, Mob)} {@code toDrop.foreach() block}
     */
    private static boolean changes(Item item) {
        return item.definition(World.getWorld()).changes;
    }

    public static long getLostItemsValue() {
        return lostItemsValue;
    }
}
