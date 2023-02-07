package com.aelous.model.content.skill.impl.farming.patch;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * Created by Situations on 2017-02-10.
 */
public class Flowers {

    public static final int FLOWER_TIMER = 125;

    public static void interactFlower(Player player, Farmbit fb) {
    }

    public static void itemOnFlowerPatch(Player player, Farmbit fb) {
    }

    public enum Data {
        MARIGOLDS(new Item(5096), 6010, 3, 8.5, 47.0, 8, 12),
        ROSEMARY(new Item(5097), 6014, 11, 12.0, 66.5, 13, 17),
        NASTURTIUM(new Item(5098), 6012, 24, 19.5, 111.0, 18, 22),
        WOAD(new Item(5099), 1793, 25, 20.5, 115.5, 23, 27),
        LIMPWURT(new Item(5100), 225, 30, 21.5, 120.0, 28, 32);

        public Item seed;
        public int product, level, startVal, endVal;
        public double plantXp, harvestXp;

        Data(Item seed, int product, int level, double plantXp, double harvestXp, int startVal, int endVal) {
            this.seed = seed;
            this.product = product;
            this.level = level;
            this.plantXp = plantXp;
            this.harvestXp = harvestXp;
            this.startVal = startVal;
            this.endVal = endVal;
        }
    }
}
