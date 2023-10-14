package com.cryptic.model.content.skill.impl.farming.impl;

import com.cryptic.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public enum Plant {

    APPLE_TREE(ItemIdentifiers.APPLE_SAPLING, ItemIdentifiers.COOKING_APPLE, 8, 8 + 13, 8 + 19, 8 + 25, 8 + 26, 27, 0, Seed.FRUIT_TREE, 22.0, 1199.0, 8.5, 7),
    BANANA_TREE(ItemIdentifiers.BANANA_SAPLING, ItemIdentifiers.BANANA, 35, 35 + 13, 35 + 19, 35 + 25, 35 + 26, 33, 0, Seed.FRUIT_TREE, 28.0, 1750.5, 10.5, 7),
    ORANGE_TREE(ItemIdentifiers.ORANGE_SAPLING, ItemIdentifiers.ORANGE, 62, 62 + 13, 62 + 19, 62 + 25, 62 + 26, 39, 0, Seed.FRUIT_TREE, 35.0, 2470.2, 13.5, 7),

    GUAM(5291, 199, 4, 173, 170, 9, 80, Seed.HERB, 11, 12, 4), //24
    MARENTILL(5292, 201, 11, 173, 170, 14, 80, Seed.HERB, 13, 15, 4), //24
    TARROMIN(5293, 203, 18, 173, 170, 19, 80, Seed.HERB, 16, 18, 4), //42
    HARRALANDER(5294, 205, 25, 173, 170, 26, 80, Seed.HERB, 21, 24, 4), //89
    RANARR(5295, 207, 32, 173, 170, 32, 80, Seed.HERB, 26, 30, 4), //35957
    TOADFLAX(5296, ItemIdentifiers.GRIMY_TOADFLAX, 39, 173, 170, 36, 80, Seed.HERB, 34, 38, 4), //5296
    IRIT(5297, 209, 46, 173, 170, 44, 80, Seed.HERB, 43, 48, 4), //89
    AVANTOE(5298, 211, 53, 173, 170, 50, 80, Seed.HERB, 54, 61, 4), //805
    KWUARM(5299, 213, 68, 173, 170, 56, 80, Seed.HERB, 69, 78, 4), //1425
    SNAPDRAGON(5300, ItemIdentifiers.GRIMY_SNAPDRAGON, 75, 173, 170, 62, 80, Seed.HERB, 87, 98, 4), //43585
    CADANTINE(5301, 215, 82, 173, 170, 67, 80, Seed.HERB, 106, 120, 4), //285
    LANTADYME(5302, ItemIdentifiers.GRIMY_LANTADYME, 89, 173, 170, 73, 80, Seed.HERB, 134, 151, 4), //967
    DWARF_WEED(5303, 217, 96, 173, 170, 79, 80, Seed.HERB, 170, 192, 4), //268
    TORSTOL(5304, 219, 103, 173, 170, 85, 80, Seed.HERB, 199, 224, 4), //19813

    POTATO(5318, 1942, 6, 0, 0, 1, 40, Seed.ALLOTMENT, 8, 9, 4),
    ONION(5319, 1957, 13, 0, 0, 5, 40, Seed.ALLOTMENT, 9, 10, 4),
    CABBAGE(5324, 1967, 20, 0, 0, 7, 40, Seed.ALLOTMENT, 10, 11, 4),
    TOMATO(5322, 1982, 27, 0, 0, 12, 40, Seed.ALLOTMENT, 12, 14, 4),
    SWEETCORN(5320, 5986, 34, 0, 0, 20, 40, Seed.ALLOTMENT, 17, 19, 6),
    STRAWBERRY(5323, 5504, 43, 0, 0, 31, 40, Seed.ALLOTMENT, 26, 29, 6),
    WATERMELON(5321, 5982, 52, 0, 0, 47, 40, Seed.ALLOTMENT, 48, 54, 8),

    MARIGOLD(5096, 6010, 8, 0, 0, 2, 60, Seed.FLOWER, 8, 47, 4),
    ROSEMARY(5097, 6014, 13, 0, 0, 11, 60, Seed.FLOWER, 12, 66, 4),
    NASTURTIUM(5098, 6012, 18, 0, 0, 24, 60, Seed.FLOWER, 19, 111, 4),
    WOAD(5099, 5738, 23, 0, 0, 25, 60, Seed.FLOWER, 20, 115, 4),
    LIMPWURT(5100, 225, 28, 0, 0, 26, 60, Seed.FLOWER, 21, 120, 4),
    ;

    public final int seed;
    public final int harvest;
    public final int healthy;
    public final int diseased;
    public final int dead;

    /**
     * The Experience gained when checking a Tree
     */
    public final double checkHealthExperience;

    /**
     * The configuration to check the health of a tree
     */
    public final int checkHealth;

    /**
     * The configuration to see a tree stump
     */
    public final int treeStump;

    public final int level;
    public final int minutes; // We no longer use OSRS timers, keep in case we ever add it back. RSPS after all
    public final byte stages;
    public final double plantExperience;
    public final double harvestExperience;
    public final Seed type;

    private static final Map<Integer, Plant> plantsBySeed = new HashMap<>();

    static {
        for (Plant plant : Plant.values()) {
            plantsBySeed.put(plant.seed, plant);
        }
    }

    public static Plant getPlantForSeed(int seed) {
        return plantsBySeed.get(seed);
    }

    public static boolean isSeed(int id) {
        return plantsBySeed.containsKey(id);
    }

    Plant(int seed, int harvest, int config, int diseased, int dead, int level, int minutes, Seed type,
          double plantExperience, double harvestExperience, int stages) {
        this.seed = seed;
        this.harvest = harvest;
        this.healthy = config;
        this.level = level;
        this.diseased = diseased;
        this.dead = dead;
        this.minutes = minutes;
        this.type = type;
        this.checkHealth = 0;
        this.checkHealthExperience = 0;
        this.treeStump = 0;
        this.plantExperience = plantExperience;
        this.harvestExperience = harvestExperience;
        this.stages = ((byte) stages);
    }

    Plant(int seed, int harvest, int config, int diseased, int dead, int treeStump, int checkHealth, int level, int minutes, Seed type,
          double plantExperience, double checkHealthExperience, double harvestExperience, int stages) {
        this.seed = seed;
        this.harvest = harvest;
        this.healthy = config;
        this.level = level;
        this.diseased = diseased;
        this.dead = dead;
        this.treeStump = treeStump;
        this.checkHealth = checkHealth;
        this.checkHealthExperience = checkHealthExperience;
        this.minutes = minutes;
        this.type = type;
        this.plantExperience = plantExperience;
        this.harvestExperience = harvestExperience;
        this.stages = ((byte) stages);
    }

    public int getMinutes() {
        return switch (type) {
            case HERB -> 7;
            case FLOWER -> 5;
            default -> 3;
        };
    }
}
