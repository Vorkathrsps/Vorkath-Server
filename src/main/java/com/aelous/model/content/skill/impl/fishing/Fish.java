package com.aelous.model.content.skill.impl.fishing;

import java.util.ArrayList;

/**
 * Created by Bart with Sensations on that enum on 12/1/2015.
 */
public enum Fish {

    SHRIMP(1, 317, 10.0, "shrimps", "some", 13516),
    KARAMBWANJI(5, 3150, 10.0, "karambwanji", "some", 12800),
    SARDINE(5, 327, 20.0, "sardine", "a", 12800),
    HERRING(10, 345, 30.0, "herring", "a", 11800),
    ANCHOVIES(15, 321, 40.0, "anchovies", "some", 10516),
    MACKEREL(16, 345, 20.0, "mackerel", "a", 8260),
    RAINBOW(38, 10138, 80.0,"rainbow", "a", 7000),
    TROUT(20, 335, 50.0, "trout", "a", 6180),
    COD(23, 341, 45.0, "cod", "a", 5960),
    PIKE(25, 349, 60.0, "pike", "a", 5079),
    SLIMY_EEL(28, 3379, 65.0, "slimy eel", "a", 4579),
    SALMON(30, 331, 70.0, "salmon", "a", 4180),
    TUNA(35, 359, 80.0, "tuna", "a", 3888),
    CAVE_EEL(38, 359, 80.0, "cave eel", "a", 3600),
    LOBSTER(40, 377, 90.0, "lobster", "a", 3612),
    BASS(46, 363, 100.0, "bass", "a", 3260),
    SWORDFISH(50, 371, 100.0, "swordfish", "a", 2888),
    LAVA_EEL(53, 2148, 30.0, "lava eel", "a", 2500),
    MONKFISH(62, 7944, 120.0, "monkfish", "a", 2358),
    KARAMBWAN(65, 3142, 105.0, "karambwan", "a", 2087),
    SHARK(76, 383, 110.0, "shark", "a", 1824),
    SEA_TURTLE(79, 395, 138.0, "sea turtle", "a", 1500), // Trawler
    INFERNAL_EEL(80, 21293, 95.0, "infernal eel", "a", 1400),
    MANTA_RAY(81, 389, 146.0, "manta ray", "a", 1300), // Trawler
    ANGLERFISH(82, 13439, 120.0, "anglerfish", "an", 1200),
    DARK_CRAB(85, 11934, 130.0, "dark crab", "a", 1100),
    SACRED_EEL(87, 13339, 105.0, "sacred eel", "an", 1000)
    ;

    public int lvl;
    public int item;
    public double xp;
    public String fishName;
    public String prefix;
    int petChance;

    Fish(int lvl, int item, double xp, String fishName, String prefix, int petChance) {
        this.lvl = lvl;
        this.item = item;
        this.xp = xp;
        this.fishName = fishName;
        this.prefix = prefix;
        this.petChance = petChance;
    }

    public ArrayList<Integer> getFishItemIds() {
        ArrayList<Integer> fishIds = new ArrayList<>();
        for (Fish fish : Fish.values()) {
            fishIds.add(fish.item);
        }
        return fishIds;
    }
}
