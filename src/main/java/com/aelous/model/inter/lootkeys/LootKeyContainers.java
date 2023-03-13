package com.aelous.model.inter.lootkeys;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.LinkedList;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.model.inter.lootkeys.LootKey.LOOT_KEY_CONTAINER_SIZE;
public class LootKeyContainers {
    private static final Logger logger = LogManager.getLogger(LootKeyContainers.class);
    private static final Marker marker = MarkerManager.getMarker("LootKeyContainers");

    public Player player;

    public LootKeyContainers(Player player) {
        this.player = player;
        lootKeyItems = new LootKey();
        keys = new LootKey[5];
        for (int i = 0; i < keys.length; i++)
            keys[i] = new LootKey();
    }

    /**
     * All the items inside the loot key.
     */
    public LootKey lootKeyItems;

    /**
     * The keys we are carrying, maximum 5.
     */
    public LootKey[] keys;

    public LootKeyContainers updateLootKey(Player killer) {
        var lostItems = player.<LinkedList<Item>>getAttribOr(LOST_ITEMS_ON_DEATH, null);
        if (lostItems != null) {
            lootKeyItems = generateLootKeyFromContainer(killer, player, lostItems);
            player.clearAttrib(LOST_ITEMS_ON_DEATH);
        }
        return this;
    }

    /**
     * Create a LootKey from the dead player's loot.
     */
    public static LootKey createLootKey(Player killer, LootKeyContainers deadInfo) {
        var rawItemsKey = deadInfo.lootKeyItems.copy(); // This is the key killer will get
        deadInfo.lootKeyItems = new LootKey();
        deadInfo.updateLootKey(killer); // Update dead player's info so their loot key has the items they'll lose when they die.
        // Create an empty key
        var rewardedLootKey = new LootKey();
        // Cycle the lost items, add them to the empty key which the Killer will get as un noted items.
        for (Item loot : rawItemsKey.lootContainer) {
            if (loot == null) continue;
            rewardedLootKey.lootContainer.add(loot.unnote(), true);
            rewardedLootKey.value += (long) loot.unnote().getValue() * loot.getAmount();
        }
        //System.out.println("createLootKey "+rewardedLootKey);
        return rewardedLootKey;
    }

    public static LootKey generateLootKeyFromContainer(Player killer, Player dead, LinkedList<Item> items) {
        var storeConsumable = killer.<Boolean>getAttribOr(LOOT_KEYS_DROP_CONSUMABLES, false);
        var keepValuables = killer.<Boolean>getAttribOr(SEND_VALUABLES_TO_LOOT_KEYS, false);
        var valuableThreshold = killer.<Integer>getAttribOr(LOOT_KEYS_VALUABLE_ITEM_THRESHOLD, 0);

        // Filter out the null items, untradeables and spawnable items.
        items.removeIf(item -> item == null || item.definition() == null || item.untradable() || item.isSpawnable());

        // Store the filtered items into a new list
        LinkedList<Item> filteredItems = new LinkedList<>();
        items.forEach(item -> {
            if ((!storeConsumable && item.definition().consumable) || (keepValuables && item.getValue() < valuableThreshold)){
                filteredItems.add(item);
            }
        });

        logger.debug(marker, "Items filtered: " + Arrays.toString(filteredItems.toArray()));

        for (Item item : filteredItems) {
            if (item == null) continue;
            var groundItem = new GroundItem(item, dead.tile(), killer).pkedFrom(dead.getUsername());
            GroundItemHandler.createGroundItem(groundItem);
        }

        filteredItems.clear(); // Clear the list after we've dropped the items.

        // Now actually filter out our settings
        items.removeIf(item -> (!storeConsumable && item.definition().consumable) || (keepValuables && item.getValue() < valuableThreshold));

        //System.out.println("Items: "+ Arrays.toString(itemsFiltered.toArray()));

        ItemContainer lostItems = new ItemContainer(LOOT_KEY_CONTAINER_SIZE, ItemContainer.StackPolicy.ALWAYS);
        int left = LOOT_KEY_CONTAINER_SIZE;
        while (left-- > 0 && !items.isEmpty()) {
            Item lost = items.peek();
            if (lost == null) {
                left++;
                items.poll();
                continue;
            }
            lostItems.add(items.poll(), true);
        }

        items.sort((o1, o2) -> {
            o1 = o1.unnote();
            o2 = o2.unnote();
            ItemDefinition def1 = o1.definition();
            ItemDefinition def2 = o2.definition();
            int v1 = def1 == null ? 0 : o1.unnote().getValue();
            int v2 = def2 == null ? 0 : o2.unnote().getValue();
            return Integer.compare(v2, v1);
        });

        long value = 0L;
        for (Item lost : lostItems) {
            if (lost == null) continue;
            value += lost.getValue();
        }

        //System.out.printf("LootKey generated. Contents count:%d, value:%d %n", lostItems.size(), value);
        return new LootKey(lostItems, value); // Create a loot key
    }

}
