package com.aelous.model.content.skill.impl.woodcutting;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.utility.Utils;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author PVE
 * @Since augustus 29, 2020
 */
public enum BirdNest {

    /* Egg */
    RED_EGG(BIRD_NEST, 5076, "search"),
    GREEN_EGG(BIRD_NEST_5071, 5078, "search"),
    BLUE_EGG(BIRD_NEST_5072, 5077, "search"),

    /* Ring */
    RING(BIRD_NEST_5074, -1, "search"),

    /* Seeds */
    SEED_ONE(BIRD_NEST_5073, -1, "search"),
    SEED_TWO(BIRD_NEST_7413, -1, "search"),
    SEED_THREE(BIRD_NEST_13653, -1, "search");

    public final int itemId, result;
    public final String optionName;

    BirdNest(int itemId, int result, String optionName) {
        this.result = result;
        this.itemId = itemId;
        this.optionName = optionName;
    }

    public static final LootTable ringNest = new LootTable().addTable(1,
        new LootItem(1635, 1, 15), //Gold ring
        new LootItem(1637, 1, 10), //Sapphire ring
        new LootItem(1639, 1, 5),  //Emerald ring
        new LootItem(1641, 1, 3),  //Ruby ring
        new LootItem(1643, 1, 1)   //Diamond ring
    );

    public static final LootTable seedNest = new LootTable().addTable(1,
        new LootItem(5312, 1, 50), //Acorn
        new LootItem(5283, 1, 50), //Apple tree seed
        new LootItem(5284, 1, 50), //Banana tree seed
        new LootItem(5285, 1, 30), //Orange tree seed
        new LootItem(5313, 1, 30), //Willow seed
        new LootItem(5286, 1, 30), //Curry tree seed
        new LootItem(5314, 1, 5),  //Maple seed
        new LootItem(5287, 1, 5),  //Pineapple seed
        new LootItem(5288, 1, 5),  //Papaya tree seed
        new LootItem(5289, 1, 5),  //Palm tree seed
        new LootItem(5290, 1, 5),  //Calquat tree seed
        new LootItem(5315, 1, 5),  //Yew seed
        new LootItem(5316, 1, 1),  //Magic seed
        new LootItem(5317, 1, 1)   //Spirit seed
    );

    private static void openNest(Player player, Item item, Item reward, String descriptiveName) {
        if (player.inventory().isFull()) {
            player.message("You don't have enough inventory space to do that.");
            return;
        }

        player.inventory().remove(item,true);
        player.inventory().add(new Item(BIRD_NEST_5075),true);
        player.inventory().add(reward);
        //TODO add achievement?
        player.message("You take " + descriptiveName + " out of the bird's nest.");
    }

    public static boolean onItemOption1(Player player, Item item) {
        for (BirdNest nest : BirdNest.values()) {
            if (item.getId() == nest.itemId) {
                if (nest.result != -1) {
                    Item reward = new Item(nest.result, 1);
                    openNest(player, item, reward, reward.definition(World.getWorld()).name.toLowerCase());
                } else {
                    Item reward = nest == RING ? ringNest.rollItem() : seedNest.rollItem();
                    openNest(player, item, reward, reward.definition(World.getWorld()).name.toLowerCase());
                }
                return true;
            }
        }
        return false;
    }

    public static Item getRandomNest(Woodcutting.Tree tree) {
        int randomNest;
        randomNest = Utils.random(BirdNest.values().length - 1);
        return new Item(BirdNest.values()[randomNest].itemId);
    }
}
