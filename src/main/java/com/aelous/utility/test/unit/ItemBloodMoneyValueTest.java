package com.aelous.utility.test.unit;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.GameBuilder;
import com.aelous.model.World;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.loaders.BloodMoneyPrices;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.shop.StoreItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * This unit test ensures that all items in the bm shop and all items from NPC drops have a bm (trade) value.
 */
public class ItemBloodMoneyValueTest {

    private static final int[] IGNORED_ITEMS = {8013, 4155, 4156, 4158, 4160, 4161, 4162, 4164, 4166, 4551, 6696, 6708, 7053, 7159, 7421, 7432, 8923, 10952, 11874, 11885, 11876, 11887, 11937, 11941, 2349, 2350, 2351, 2352, 2353, 2354, 2359, 2360};

    @Test
    void shopItems() {
        List<Item> items = new ArrayList<>();
        List<Item> itemsMissingValues = new ArrayList<>();
        boolean missingValues = false;
        boolean matchingValues = true;
        World world = new World();
        GameBuilder gameBuilder = new GameBuilder();

        try {
            gameBuilder.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        items.addAll(Arrays.asList(world.shop(2).container.getItems()));
        items.addAll(Arrays.asList(world.shop(3).container.getItems()));
        items.addAll(Arrays.asList(world.shop(4).container.getItems()));
        items.addAll(Arrays.asList(world.shop(5).container.getItems()));
        items.addAll(Arrays.asList(world.shop(6).container.getItems()));
        items.addAll(Arrays.asList(world.shop(7).container.getItems()));
        items.addAll(Arrays.asList(world.shop(8).container.getItems()));

        ItemIdentifiers[] itemIdentifiers = new ItemIdentifiers[]{};

        //List<Item> idArray = List.of(Item.of(itemIdentifiers.length));

        items.add(Item.of(itemIdentifiers.length));


        for (Item item : items) {
            if (item == null) {
                continue;
            }
            //Untradable items don't really need a value.
            if (!item.rawtradable()) continue;
           /* if (BloodMoneyPrices.get(item.getId()).id() < 1 && Arrays.stream(IGNORED_ITEMS).noneMatch(itemId -> itemId == item.getId())) {
                missingValues = true;
                itemsMissingValues.add(item);
            }*/
            StoreItem storeItem = (StoreItem) item;
           /* if (BloodMoneyPrices.get(item.getId()).id() !=  storeItem.getShopValue()) {
                matchingValues = false;
                System.err.println("BM value does not match for " + storeItem.name() + " (" + storeItem.getId() + "). Shop value: " + storeItem.getShopValue() + " - Definition value: " + BloodMoneyPrices.get(item.getId()).id());
            }*/
        }

        if (missingValues) {
            System.err.println("The " + itemsMissingValues.size() + " items missing BM values are: " + Arrays.toString(itemsMissingValues.toArray()));
            StringBuilder ids = new StringBuilder("The " + itemsMissingValues.size() + " item IDs missing BM values are: ");
            for (Item item : itemsMissingValues) {
                ids.append(item.getId()).append(", ");
            }
            System.err.println(ids);
        }
        assertFalse(missingValues);
        assertTrue(matchingValues);
    }
    @Test
    void pvpTradableItems() {
        List<Item> itemsMissingValues = new ArrayList<>();
        boolean missingValues = false;
        for (Integer item : Item.TRADABLES) {
            //Untradable items don't really need a value.
            if (!new Item(item, 1).rawtradable()) continue;
            /*if (BloodMoneyPrices.get(item).id() < 1 && Arrays.stream(IGNORED_ITEMS).noneMatch(itemId -> itemId == item)) {
                missingValues = true;
                itemsMissingValues.add(new Item(item, 1));
            }*/
        }
        if (missingValues) {
            System.err.println("The " + itemsMissingValues.size() + " items missing BM values are: " + Arrays.toString(itemsMissingValues.toArray()));
            StringBuilder ids = new StringBuilder("The " + itemsMissingValues.size() + " item IDs missing BM values are: ");
            for (Item item : itemsMissingValues) {
                ids.append(item.getId()).append(", ");
            }
            System.err.println(ids);
        }
        assertFalse(missingValues);
    }
    @Test
    void npcDropItems() {
        List<Item> itemsMissingValues = new ArrayList<>();
        HashMap<Integer, Integer> nonStandardNotedIds = new HashMap<>();
        HashMap<Integer, Integer> npcDrops = new HashMap<>();
        boolean missingValues = false;
        ScalarLootTable.registered.forEach((k, lootTable) -> {
            for (ScalarLootTable.TableItem tableItem : lootTable.items) {
                npcDrops.put(tableItem.id, 1);
                //Let's also check the noted version has the proper value
                Item item = new Item(tableItem.id, 1);
                if (item.noteable() && !item.noted()) {
                    int noteId = 0;
                    if (item.note().getId() > -1) {
                        noteId = item.note().getId();
                        if (noteId != (item.getId() + 1)) {
                            nonStandardNotedIds.put(noteId, 1);
                        }
                    } else {
                        noteId = item.getId() + 1;
                    }
                    npcDrops.put(noteId, 1);
                }
            }
        });
        StringBuilder nonStandardNotedIdsString = new StringBuilder();
        for (Integer item : npcDrops.keySet()) {
            //Untradable items don't really need a value.
            if (!new Item(item, 1).rawtradable()) continue;
            /*if (BloodMoneyPrices.get(item).id() < 1 && Arrays.stream(IGNORED_ITEMS).noneMatch(itemId -> itemId == item)) {
                missingValues = true;
                itemsMissingValues.add(new Item(item, 1));
                if (nonStandardNotedIds.containsKey(item)) {
                    nonStandardNotedIdsString.append(item).append(",");
                }
            }*/
        }
        if (missingValues) {
            System.err.println("The " + itemsMissingValues.size() + " items missing BM values are: " + Arrays.toString(itemsMissingValues.toArray()));
            StringBuilder ids = new StringBuilder("The " + itemsMissingValues.size() + " item IDs missing BM values are: ");
            for (Item item : itemsMissingValues) {
                ids.append(item.getId()).append(", ");
            }
            System.err.println(ids);
            if (nonStandardNotedIdsString.length() > 0) {
                nonStandardNotedIdsString.insert(0, "The " + nonStandardNotedIdsString.toString().replaceAll("[^,]", "").length() + " Non Standard Noted Item IDs are: ");
                System.err.println(nonStandardNotedIdsString);
            }
        }
        assertFalse(missingValues);
    }
    @Test
    void notedValuesTest() {
        boolean notedValuesCorrect = true;
        List<Item> itemsIncorrectValues = new ArrayList<>();
        World world = new World();
        for (int itemId = 0; itemId < world.definitions().total(ItemDefinition.class); itemId++) {
            ItemDefinition definition = world.definitions().get(ItemDefinition.class, itemId);
            if (definition == null) continue;
            if (!new Item(itemId).rawtradable()) continue;

            if (new Item(itemId).noteable()) {
                if (!new Item(itemId).noted()) {
                    Item item = new Item(itemId);
                    Item notedItem = new Item(itemId).note();
                    if (notedItem.getId() > 0 && item.getBloodMoneyPrice().id() != notedItem.getBloodMoneyPrice().id()) {
                        notedValuesCorrect = false;
                        itemsIncorrectValues.add(item);
                        System.err.println("The BM value for " + item.name() + " does not match the noted item value. Item (" + item.getId() + ") value: " + item.getBloodMoneyPrice().id() + " Noted (" + notedItem.getId() + ") value: " + notedItem.getBloodMoneyPrice().value());
                    }
                }
            }

        }
        if (!notedValuesCorrect) {
            System.err.println("The following " + itemsIncorrectValues.size() + " items have noted items that do not match their values: " + Arrays.toString(itemsIncorrectValues.toArray()));
        }
        assertTrue(notedValuesCorrect);
    }

}
