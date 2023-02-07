package com.aelous.model.content.skill.impl.farming.patch;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bart on 4/24/2016.
 *
 * Base object for farming allotments, including data.
 */
public class Allotments {

    public static final int ALLOTMENT_TIMER = 250;

    public static void interactAllotment(Player player, Farmbit varbit) {
        /*var varval = player.varps().farmbit(varbit);
        var states = AllotmentInfo.decompose(varval);
        var growing = RANGELOOKUP[states.stage];*/
    }

    public static void itemOnAllotment(Player player, Farmbit fb) {
    }

    public enum Data {
        POTATOES(new Item(5318, 3), 1942, 1, 0.30, 8.0, 9.0, 6, 10, 49000, Flowers.Data.MARIGOLDS),
        ONIONS(new Item(5319, 3), 1957, 5, 0.30, 9.5, 10.5, 13, 17, 37000, Flowers.Data.MARIGOLDS),
        CABBAGES(new Item(5324, 3), 1965, 7, 0.25, 10.0, 11.5, 20, 24, 32000, Flowers.Data.ROSEMARY),
        TOMATOES(new Item(5322, 3), 1982, 12, 0.20, 12.5, 14.0, 27, 31, 24000, Flowers.Data.MARIGOLDS),
        SWEETCORNS(new Item(5320, 3), 5986, 20, 0.17, 17.0, 19.0, 34, 40, 19900),
        STRAWBERRIES(new Item(5323, 3), 5504, 31, 0.14, 26.0, 29.0, 43, 49, 14000),
        WATERMELONS(new Item(5321, 3), 5982, 47, 0.10, 48.5, 54.5, 52, 60, 9000, Flowers.Data.NASTURTIUM);

        public Item seed;
        public int product, level, startVal, endVal, petOdds;
        public double diseaseOdds, plantXp, harvestXp;
        public Flowers.Data protector = null;

        Data(Item seed, int product, int level, double diseaseOdds, double plantXp, double harvestXp, int startVal, int endVal, int petOdds, Flowers.Data protector) {
            this.seed = seed;
            this.product = product;
            this.level = level;
            this.diseaseOdds = diseaseOdds;
            this.plantXp = plantXp;
            this.harvestXp = harvestXp;
            this.startVal = startVal;
            this.endVal = endVal;
            this.petOdds = petOdds;
            this.protector = protector;
        }

        Data(Item seed, int product, int level, double diseaseOdds, double plantXp, double harvestXp, int startVal, int endVal, int petOdds) {
            this.seed = seed;
            this.product = product;
            this.level = level;
            this.diseaseOdds = diseaseOdds;
            this.plantXp = plantXp;
            this.harvestXp = harvestXp;
            this.startVal = startVal;
            this.endVal = endVal;
            this.petOdds = petOdds;
        }

        //TODO from here have Jak check
        public Range<Integer> range() {
         return Range.closed(startVal, endVal + 2);
        }
    }

    // Range lookup map to facilitate looking up the values to their data objects.
    private static ImmutableRangeMap.Builder<Integer, Data> RANGELOOKUP = ImmutableRangeMap.builder();

    // Seed lookup map
    private static Map<Integer, Data> SEEDLOOKUP = new HashMap<Integer, Data>();

    static {
        for (Data data : Data.values()) {
            RANGELOOKUP.put(data.range(), data).build();
        }
        for (Data data : Data.values()) {
            SEEDLOOKUP.put(data.seed.getId(), data);
        }
    }

}
