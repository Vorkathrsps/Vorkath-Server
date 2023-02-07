package com.aelous.model.content.skill.impl.farming.patch;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * Created by Bart on 6/4/2016.
 */
public class Herbs {

    public static final int HERB_TIMER = 500;

    public static void interactHerb(Player player, Farmbit fb) {
    }

    public static void itemOnHerbPatch(Player player, Farmbit fb) {
    }

    public enum Data {

        GUAM(new Item(5291), 199, 9, 0.13, 11.0, 12.5, 4, 8),
        MARRENTIL(new Item(5292), 201, 14, 0.13, 13.5, 15.0, 11, 15),
        TARROMIN(new Item(5293), 203, 19, 0.12, 16.0, 18.0, 18, 22),
        HARRALANDER(new Item(5294), 205, 26, 0.12, 21.5, 24.0, 25, 29),
        RANARR(new Item(5295), 207, 32, 0.11, 27.0, 30.5, 32, 36),
        TOADFLAX(new Item(5296), 3049, 38, 0.11, 34.0, 38.5, 39, 43),
        IRIT(new Item(5297), 209, 44, 0.10, 43.0, 48.5, 46, 50),
        AVANTOE(new Item(5298), 211, 50, 0.10, 54.5, 61.5, 53, 57),
        KWUARM(new Item(5299), 213, 56, 0.10, 69.0, 78.0, 68, 72),
        SNAPDRAGON(new Item(5300), 3051, 62, 0.09, 87.5, 98.5, 75, 79),
        CADANTINE(new Item(5301), 215, 67, 0.09, 106.5, 120.0, 82, 86),
        LANTADYME(new Item(5302), 2485, 73, 0.09, 134.5, 151.5, 89, 93),
        DWARF_WEED(new Item(5303), 217, 79, 0.09, 170.5, 192.5, 96, 100),
        TORSTOL(new Item(5304), 219, 85, 0.08, 199.5, 224.5, 103, 107)
        ;

        public Item seed;
        public int product, level, startVal, endVal;
        public double diseaseOdds, plantXp, harvestXp;

       Data(Item seed, int product, int level, double diseaseOdds, double plantXp, double harvestXp, int startVal, int endVal) {
            this.seed = seed;
            this.product = product;
            this.level = level;
            this.diseaseOdds = diseaseOdds;
            this.plantXp = plantXp;
            this.harvestXp = harvestXp;
            this.startVal = startVal;
            this.endVal = endVal;
        }
    }
}
