package com.aelous.model.content.areas.wilderness.slayer;

import com.aelous.model.World;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @Author Origin
 * @Date 3/24/2023
 */
public class WildernessSlayerCasket {

    /**
     * The Player Recieving Their Casket
     */
    private final Player player;

    public WildernessSlayerCasket(Player player) {
        this.player = player;
    }

    /**
     * The Supply Loot Drop Generator
     *
     * @param npc
     */
    private void dropSupplys(NPC npc) {
        var taskID = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(player.slayerTaskId());
        if (task != null && Slayer.creatureMatches(player, npc.id())) {
            if (task.matches(taskID)) {
                GroundItem groundItem = new GroundItem(new Item(Objects.requireNonNull(supplyLoot())), npc.tile(), player);
                player.message(Color.RED.wrap("<img=2010>You've recieved a supply loot drop!"));
                for (int lootIndex = 0; lootIndex < 3; lootIndex++) {
                    GroundItemHandler.createGroundItem(groundItem);
                    player.message(Color.RED.wrap(groundItem.getItem().getAmount() + "X: " + groundItem.getItem().name()));
                }
            }
        }
    }

    /**
     * The Slayer Casket Loot Generator
     *
     * @param npc
     */
    private void dropCasket(NPC npc) {
        var taskID = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(player.slayerTaskId());
        if (task != null && Slayer.creatureMatches(player, npc.id())) {
            if (task.matches(taskID)) {
                GroundItem groundItem = new GroundItem(new Item(ItemIdentifiers.ANCIENT_CASKET), npc.tile(), player);
                GroundItemHandler.createGroundItem(groundItem);
                player.message(Color.RED.wrap("<img=2010>A slayer casket has appeared!"));
                World.getWorld().sendWorldMessage(player.getDisplayName() + " has received " + groundItem.getItem().name() + " at wilderness level " + WildernessArea.wildernessLevel(player.tile()));
            }
        }
    }

    /**
     * The Roll For The Casket Drop Rate While On Task
     *
     * @param npc
     * @return
     */
    public boolean rollForCasket(NPC npc) {
        if (Utils.rollDie(50, 1)) {
            dropCasket(npc);
        }
        return false;
    }

    /**
     * The Roll For The Supply Loot Drop Rate While On Task
     *
     * @param npc
     * @return
     */
    public boolean rollForSupplys(NPC npc) {
        if (Utils.rollDie(25, 1)) {
            dropSupplys(npc);
        }
        return false;
    }

    /**
     * They ArrayList Loot Picker For Supply Drops
     *
     * @return
     */
    private Item supplyLoot() {
        return Utils.randomElement(LOOT);
    }

    /**
     * They ArrayList Loot Picker For Caskets
     *
     * @return
     */
    private Item supplyCasketLoot() { //TODO
        return Utils.randomElement(CASKET_LOOT);
    }

    private static final List<Item> CASKET_LOOT = Arrays.asList(
    );
    /**
     * The Loot ArrayList
     */
    private static final List<Item> LOOT = Arrays.asList(
        new Item(13442, World.getWorld().random(25, 100)),
        new Item(6686, World.getWorld().random(1, 10)),
        new Item(3025, World.getWorld().random(1, 5)),
        new Item(BLIGHTED_ANCIENT_ICE_SACK, 150),
        new Item(BLIGHTED_SNARE_SACK, 150),
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
