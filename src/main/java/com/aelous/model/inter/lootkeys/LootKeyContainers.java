package com.aelous.model.inter.lootkeys;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        //must be cleared before it gets here
        if (lostItems != null) {//nope its not null
            System.out.println("got here, create a key"); //lost items is null, bcyuz if the attribute thats why i sai d i needed items on death, false I just showed u mine u just did it wrong then
            System.out.println("lost items : "+lostItems);
            lootKeyItems = generateLootKeyFromContainer(killer, player, lostItems);
            player.clearAttrib(LOST_ITEMS_ON_DEATH);
        }
        return this;
    }

    /**
     * Create a LootKey from the dead player's loot.
     */
    public static @NotNull LootKey createLootKey(Player killer, @NotNull LootKeyContainers deadInfo) {
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
            System.out.println("Added item " + loot);
        }
        System.out.println("createLootKey " + rewardedLootKey);
        return rewardedLootKey;
    }

    @Contract("_, _, _ -> new")//whats hits? it all hits, it creates the key it just doesnt put the items in it lmao
    public static @NotNull LootKey generateLootKeyFromContainer(@NotNull Player killer, Player dead, @NotNull LinkedList<Item> items) {
        System.out.println("got here??");//The items are just never send every aarray is empty
        System.out.println("still got lost items "+items);
        var storeConsumable = killer.<Boolean>getAttribOr(LOOT_KEYS_DROP_CONSUMABLES, false);
        var keepValuables = killer.<Boolean>getAttribOr(SEND_VALUABLES_TO_LOOT_KEYS, false);
        var valuableThreshold = killer.<Integer>getAttribOr(LOOT_KEYS_VALUABLE_ITEM_THRESHOLD, 0);

        // Filter out the null items, untradeables and spawnable items.
        items.removeIf(item -> {
            if (item.untradable() || item.isSpawnable()) {
                var groundItem = new GroundItem(item, dead.tile(), killer).pkedFrom(dead.getUsername());
                GroundItemHandler.createGroundItem(groundItem);
                logger.info("reject {}", item.definition());
                return true;
            }

            if ((!storeConsumable && item.definition().consumable) || (keepValuables && item.getValue() < valuableThreshold)) {
                var groundItem = new GroundItem(item, dead.tile(), killer).pkedFrom(dead.getUsername());
                GroundItemHandler.createGroundItem(groundItem);
                return true;
            }
            return false;
        });

        System.out.println("currently lost items "+items);

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
            System.out.println("added to container "+lost);
        }

        items.sort((o1, o2) -> {
            o1 = o1.unnote();
            o2 = o2.unnote();
            ItemDefinition def1 = o1.definition(World.getWorld());
            ItemDefinition def2 = o2.definition(World.getWorld());
            int v1 = def1 == null ? 0 : o1.unnote().getValue();
            int v2 = def2 == null ? 0 : o2.unnote().getValue();
            return Integer.compare(v2, v1);
        });

        long value = 0L;
        for (Item lost : lostItems) {
            if (lost == null) continue;
            value += lost.getValue();
        }

        System.out.printf("LootKey generated. Contents count:%d, value:%d %n", lostItems.size(), value);
        return new LootKey(lostItems, value); // Create a loot key
    }

}
