package com.cryptic.model.content.skill.impl.farming.impl;

import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @author Sharky
 * @Since June 16, 2023
 */
public enum Patch {

    CATHERBY_ALLOTMENT_NORTH(new Tile(2805, 3465), new Tile(2815, 3469), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    CATHERBY_ALLOTMENT_SOUTH(new Tile(2805, 3458), new Tile(2815, 3461), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    CATHERBY_HERB(new Tile(2813, 3462), new Tile(2815, 3464), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.HERB),
    CATHERBY_FLOWER(new Tile(2808, 3462), new Tile(2811, 3465), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.FLOWER),

    FALADOR_HERB(new Tile(3058, 3310), new Tile(3060, 3313), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.HERB),
    FALADOR_FLOWER(new Tile(3054, 3306), new Tile(3056, 3307), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.FLOWER),
    FALADOR_ALLOTMENT_NORTH(new Tile(3050, 3306), new Tile(3055, 3312), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    FALADOR_ALLOTMENT_SOUTH(new Tile(3055, 3302), new Tile(3059, 3309), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),

    ARDOUGNE_HERB(new Tile(2670, 3374), new Tile(2671, 3375), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.HERB),
    ARDOUGNE_FLOWER(new Tile(2666, 3374), new Tile(2667, 3375), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.FLOWER),
    ARDOUGNE_ALLOTMENT_NORTH(new Tile(2662, 3377), new Tile(2671, 3379), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    ARDOUGNE_ALLOTMENT_SOUTH(new Tile(2662, 3370), new Tile(2671, 3372), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),

    PHAS_HERB(new Tile(3605, 3529), new Tile(3606, 3530), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.HERB),
    PHAS_FLOWER(new Tile(3601, 3525), new Tile(3602, 3526), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.FLOWER),
    PHAS_ALLOTMENT_WEST(new Tile(3597, 3525), new Tile(3601, 3530), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    PHAS_ALLOTMENT_EAST(new Tile(3602, 3521), new Tile(3606, 3526), new Animation(2275), ItemIdentifiers.SEED_DIBBER, ItemIdentifiers.SECATEURS, Seed.ALLOTMENT),
    LUMBRIDGE_TREE(new Tile(3191, 3229, 0), new Tile(3195, 3233, 0), new Animation(-1), -2, -2, Seed.TREE),
    VARROCK_TREE(new Tile(3227, 3457, 0), new Tile(3231, 3461, 0), new Animation(-1), ItemIdentifiers.SEED_DIBBER, -2, Seed.TREE),
    FALADOR_TREE(new Tile(3002, 3371, 0), new Tile(3006, 3375, 0), new Animation(-1), -2, -2, Seed.TREE),
    TAVERLY_TREE(new Tile(2934, 3436, 0), new Tile(2938, 3440, 0), new Animation(-1), -2, -2, Seed.TREE),
    STRONGHOLD_TREE(new Tile(2434, 3413, 0), new Tile(2438, 3417, 0), new Animation(-1), -2, -2, Seed.TREE),

    STRONGHOLD_FRUIT_TREE(new Tile(2474, 3444, 0), new Tile(2477, 3447, 0), new Animation(-1), -2, -2, Seed.FRUIT_TREE),
    CATHERBY_FRUIT_TREE(new Tile(2859, 3432, 0), new Tile(2862, 3435, 0), new Animation(-1), -2, -2, Seed.FRUIT_TREE),
    MAZE_FRUIT_TREE(new Tile(2488, 3178, 0), new Tile(2491, 3181, 0), new Animation(-1), -2, -2, Seed.FRUIT_TREE),
    BRIMHAVEN_FRUIT_TREE(new Tile(2763, 3211, 0), new Tile(2766, 3214, 0), new Animation(-1), -2, -2, Seed.FRUIT_TREE),
    LLETYA_FRUIT_TREE(new Tile(2345, 3160, 0), new Tile(2348, 3163, 0), new Animation(-1), -2, -2, Seed.FRUIT_TREE),

    PORT_SARIM_SPIRIT_TREE(new Tile(3058, 3256, 0), new Tile(3062, 3260, 0), new Animation(-1), -2, -2, Seed.SPIRIT_TREE),
    BRIMHAVEN_SPIRIT_TREE(new Tile(2800, 3201, 0), new Tile(2804, 3205, 0), new Animation(-1), -2, -2, Seed.SPIRIT_TREE),
    ETCETRIA_SPIRIT_TREE(new Tile(2611, 3856, 0), new Tile(2615, 3860, 0), new Animation(-1), -2, -2, Seed.SPIRIT_TREE),

    /**
     * StrongHold Fruit tree - 2473, 3443, 0
     * Maze Fruit Tree - 2489, 3182, 0
     * Catherby Fruit Tree - 2858, 3430, 0
     * Brimhaven Fruit tree - 2767, 3213, 0
     * Lleya Fruit Tree - 2344, 3165, 0
     *
     *
     * Port Sarim Spirit Tree - 3063, 3259, 0
     * Brimhaven Spirit Tree - 2802, 3206, 0
     * Etcetria Spirit Tree - 2613, 3854, 0
     *
     *
     * StrongHold Tree - 2436, 3412, 0
     * Lumbridge Tree - 3196, 3231, 0
     * Varrock Tree - 3227, 3456, 0
     * Falador Tree - 3003, 3376, 0
     * Taverly Tree - 2933, 3438,
     */

    ;

    public final Tile bottomLeft;
    public final Tile topRight;
    public final Animation harvestAnimation;
    public final int harvestItem;
    public final int planter;
    public final Seed seed;

    Patch(Tile bottomLeft, Tile topRight, Animation harvestAnimation, int planter, int harvestItem, Seed Seed) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.harvestItem = harvestItem;
        this.harvestAnimation = harvestAnimation;
        this.planter = planter;
        this.seed = Seed;
    }
}
