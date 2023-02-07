package com.aelous.model.content.skill.impl.cooking;

import com.aelous.utility.ItemIdentifiers;

/**
 * @author Patrick van Elderen | February, 07, 2021, 09:26
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum Cookable {
    //Raw Fish
    RAW_SHRIMPS(1, 30.0, 317, 315, 7954, "a shrimp", "shrimps"),
    RAW_KARAMBWANJI(1, 15.0, 3150, 3151, 592, "a karambwanji", "karambwanji"),
    RAW_SARDINE(1, 40.0, 327, 325, 369, "a sardine", "sardines"),
    RAW_HERRING(5, 50.0, 345, 347, 357, "a herring", "herring"),
    RAW_ANCHOVIES(1, 30.0, 321, 319, 323, "an anchovy", "anchovies"),
    RAW_KARAMBWAN(1, 190.0, 3142, 3144, 3148, "a karambwan", "karambwan"),
    RAW_MACKEREL(10, 60.0, 345, 347, 357, "a mackerel", "mackerels"),
    RAW_TROUT(15, 70.0, 335, 333, 343, "a trout", "trouts"),
    RAW_COD(18, 75.0, 341, 339, 343, "a cod", "cods"),
    RAW_PIKE(20, 80.0, 349, 351, 343, "a pike", "pikes"),
    RAW_SLIMY_EEL(28, 95.0, 3379, 3381, 3383, "a slimy eel", "slimy eels"),
    RAW_SALMON(25, 90.0, 331, 329, 343, "a salmon", "salmons"),
    RAW_TUNA(30, 100.0, 359, 361, 367, "a tuna", "tunas"),
    RAW_CAVE_EEL(28, 115.0, 359, 361, 367, "a cave eel", "cave eels"),
    RAW_LOBSTER(40, 120.0, 377, 379, 381, "a lobster", "lobsters"),
    RAW_BASS(43, 130.0, 363, 365, 367, "a bass", "basses"),
    RAW_SWORDFISH(45, 140.0, 371, 373, 375, "a swordfish", "swordfishes"),
    RAW_LAVA_EEL(53, 30.0, 2148, 2149, 3383, "a lava eel", "lava eels"),
    RAW_MONKFISH(62, 150.0, 7944, 7946, 7948, "a monkfish", "monkfishes"),
    RAW_SHARK(80, 210.0, 383, 385, 387, "a shark", "sharks"),
    RAW_SEA_TURTLE(82, 211.3, 395, 397, 399, "a sea turtle", "sea turtles"),
    RAW_ANGLERFISH(84, 230.0, 13439, 13441, 13443, "an anglerfish", "anglerfish"),
    RAW_DARK_CRAB(85, 215.0, 11934, 11936, 11938, "a dark crab", "dark crabs", 15),
    RAW_MANTA_RAY(91, 216.2, 389, 391, 393, "a manta ray", "manta rays"),

    //Raw Meat
    RAW_MEAT(1, 30.0, 2132, 2142, 2146, "a piece of meat", "meat"),
    RAW_RAT_MEAT(1, 30.0, 2134, 2142, 2146, "a piece of rat meat", "rat meat"),
    RAW_YAK_MEAT(1, 30.0, 10816, 2142, 2146, "a piece of yak meat", "yak meat"),
    RAW_BEAR_MEAT(1, 30.0, 2136, 2142, 2146, "a piece of bear meat", "bear meat"),
    RAW_CHICKEN(1, 30.0, 2138, 2140, 2144, "a chicken", "chickens", 10),
    RAW_RABBIT(1, 30.0, 3226, 3228, 7222, "a rabbit", "rabbits"),
    CRAB_MEAT(21, 100.0, 7518, 7521, 7520, "a piece of crab meat", "crab meat"),

    //Pies
    REDBERRY_PIE(10, 78.0, 2321, 2325, 2329, "a redberry pie"),
    PIE_MEAT(20, 104.0, 2317, 2327, 2329, "a meat pie"),
    MUD_PIE(29, 128.0, 2319, 7170, 2329, "a mud pie"),
    APPLE_PIE(30, 130.0, 7168, 2323, 2329, "an apple pie"),
    GARDEN_PIE(34, 128.0, 7186, 7188, 2329, "a garden pie"),
    FISH_PIE(47, 164.0, 7186, 7188, 2329, "a fish pie"),
    ADMIRAL_PIE(70, 210.0, 7196, 1798, 2329, "an admiral pie"),
    WILD_PIE(85, 240.0, 7206, 7208, 2329, "a wild pie"),
    SUMMER_PIE(95, 260.0, 7216, 7218, 2329, "a summer pie"),

    //Pizza
    PLAIN_PIZZA(35, 143.0, 2287, 2289, 2305, "a plain pizza"),

    //Cake
    ISHCAKE(31, 100.0, 7529, 7530, 7531, "a fishcake"),
    CAKE(40, 180.0, 1889, 1891, 1903, "a cake"),

    //Random
    SWEET_CORN(28, 104.0, 5986, 5988, 5990, "a piece of sweet corn"),
    SEAWEED(1, 0.0, 401, 1781, 1781, "soda ash"),
    BREAD(1,40.0, ItemIdentifiers.BREAD_DOUGH, ItemIdentifiers.BREAD, ItemIdentifiers.BURNT_BREAD, "bread"),
    STEW(25, 0.0, ItemIdentifiers.UNCOOKED_STEW, ItemIdentifiers.STEW, ItemIdentifiers.BURNT_STEW, "stew"),
    CURRY(60, 280.0,ItemIdentifiers.UNCOOKED_CURRY, ItemIdentifiers.CURRY, ItemIdentifiers.BURNT_CURRY,"curry"),
    POTATO(7, 15.0,ItemIdentifiers.POTATO, ItemIdentifiers.BAKED_POTATO, ItemIdentifiers.BURNT_POTATO,"potato");

    public final int lvl;
    public final double exp;
    public final int raw;
    public final int cooked;
    public final int burnt;
    public final String itemname;
    public String plural;
    public int offset = 3;

    Cookable(int lvl, double exp, int raw, int cooked, int burnt, String itemname, String plural) {
        this.lvl = lvl;
        this.exp = exp;
        this.raw = raw;
        this.cooked = cooked;
        this.burnt = burnt;
        this.itemname = itemname;
        this.plural = plural;
    }

    Cookable(int lvl, double exp, int raw, int cooked, int burnt, String itemname) {
        this.lvl = lvl;
        this.exp = exp;
        this.raw = raw;
        this.cooked = cooked;
        this.burnt = burnt;
        this.itemname = itemname;
    }

    Cookable(int lvl, double exp, int raw, int cooked, int burnt, String itemname, String plural, int offset) {
        this.lvl = lvl;
        this.exp = exp;
        this.raw = raw;
        this.cooked = cooked;
        this.burnt = burnt;
        this.itemname = itemname;
        this.plural = plural;
        this.offset = offset;
    }

    public static Cookable get(int raw) {
        for (Cookable cookable : Cookable.values()) {
            if (cookable.raw == raw) {
                return cookable;
            }
        }
        return null;
    }
}
