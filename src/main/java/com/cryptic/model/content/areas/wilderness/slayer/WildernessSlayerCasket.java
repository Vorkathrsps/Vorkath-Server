package com.cryptic.model.content.areas.wilderness.slayer;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 * @Date 3/24/2023
 */
public class WildernessSlayerCasket {

    /**
     * The Supply Loot Drop Generator
     *
     * @param npc
     */
    private void dropSupplys(@NotNull final Player player, @NotNull NPC npc) {
        player.message(Color.RED.wrap("<lsprite=2010>You've received a supply loot drop!"));
        for (var items : supplyLoot().entrySet()) {
            final int id = items.getKey();
            final int amount = items.getValue();
            Item item = new Item(id, amount);
            GroundItem groundItem = new GroundItem(item, npc.tile(), player);
            GroundItemHandler.createGroundItem(groundItem);
            var name = item.noted() ? item.unnote().name() : item.name();
            player.message(Color.RED.wrap(item.getAmount() + "X: " + name));
        }
    }


    /**
     * The Slayer Casket Loot Generator
     *
     * @param npc
     */
    private void dropCasket(@NotNull final Player player, NPC npc) {
        var taskID = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(player.slayerTaskId());
        if (task != null && Slayer.creatureMatches(player, npc.id())) {
            if (task.matches(taskID)) {
                GroundItem groundItem = new GroundItem(new Item(ItemIdentifiers.ANCIENT_CASKET), npc.tile(), player);
                GroundItemHandler.createGroundItem(groundItem);
                player.message(Color.RED.wrap("<lsprite=2010>A slayer casket has appeared!"));
                World.getWorld().sendWorldMessage(player.getDisplayName() + " has received " + groundItem.getItem().name() + " at wilderness level " + WildernessArea.getWildernessLevel(player.tile()));
            }
        }
    }

    /**
     * The Roll For The Casket Drop Rate While On Task
     *
     * @param npc
     */
    public void rollForCasket(@NotNull final Player player, NPC npc) {
        if (Utils.rollDie(50, 1)) {
            dropCasket(player, npc);
        }
    }

    /**
     * The Roll For The Supply Loot Drop Rate While On Task
     *
     * @param npc
     */
    public void rollForSupplys(@NotNull final Player player, NPC npc) {
        int chance = 75;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER -> chance = 70;
            case SAPPHIRE_MEMBER -> chance = 65;
            case EMERALD_MEMBER -> chance = 60;
            case DIAMOND_MEMBER -> chance = 55;
            case DRAGONSTONE_MEMBER -> chance = 50;
            case ONYX_MEMBER -> chance = 45;
            case ZENYTE_MEMBER -> chance = 40;
        }
        if (Utils.rollDie(chance, 1)) {
            dropSupplys(player, npc);
        }
    }

    /**
     * They ArrayList Loot Picker For Supply Drops
     *
     * @return
     */
    private Map<Integer, Integer> supplyLoot() {
        List<Item> loot = new ArrayList<>(LOOT);
        Collections.shuffle(LOOT);
        Map<Integer, Integer> items = new HashMap<>();
        for (int i = 0; i < 3 && i < loot.size(); i++) {
            Item item = loot.get(i);
            items.put(item.getId(), item.getAmount());
        }
        return items;
    }


    /**
     * They ArrayList Loot Picker For Caskets
     *
     * @return
     */
    private Item supplyCasketLoot() { //TODO
        return Utils.randomElement(CASKET_LOOT);
    }

    private static final List<Item> CASKET_LOOT = List.of();
    /**
     * The Loot ArrayList
     */
    private static final List<Item> LOOT = Arrays.asList(
        new Item(COINS_995, World.getWorld().random(20000, 250000)),
        new Item(ANGLERFISH + 1, World.getWorld().random(25, 100)),
        new Item(SARADOMIN_BREW4 + 1, World.getWorld().random(1, 10)),
        new Item(SUPER_RESTORE4 + 1, World.getWorld().random(1, 5)),
        new Item(BLIGHTED_ANCIENT_ICE_SACK, 150),
        new Item(BLIGHTED_ENTANGLE_SACK, 150),
        new Item(BLIGHTED_TELEPORT_SPELL_SACK, 150),
        new Item(BLIGHTED_SURGE_SACK, 50),
        new Item(1618, 10),
        new Item(1632, 10),
        new Item(1620, 25),
        new Item(1624, 25),
        new Item(1622, 25),
        new Item(CRYSTAL_KEY),
        new Item(208, World.getWorld().random(5, 10)),
        new Item(218, World.getWorld().random(5, 10)),
        new Item(226, World.getWorld().random(5, 10)),
        new Item(206, World.getWorld().random(5, 10)),
        new Item(12626, World.getWorld().random(1, 3)),
        new Item(3139, World.getWorld().random(6, 9)),
        new Item(6694, World.getWorld().random(6, 9)),
        new Item(224, World.getWorld().random(6, 9)),
        new Item(12696, World.getWorld().random(1, 3)),
        new Item(270, World.getWorld().random(5, 10))
    );

    /**
     * The Casket Item Interaction
     *
     * @param player the player
     * @param item   the item
     * @return
     */
    public boolean open(Player player, Item item) {
        if (item.getId() == ItemIdentifiers.ANCIENT_CASKET) {
            if (player.getInventory().contains(ItemIdentifiers.ANCIENT_CASKET)) {
                player.getInventory().remove(ItemIdentifiers.ANCIENT_CASKET);
                player.getInventory().add(supplyCasketLoot());
                player.message("You recieved a: " + supplyCasketLoot().name());
            }
            return true;
        }
        return false;
    }
}
