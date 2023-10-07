package com.cryptic.model.content.skill.impl.woodcutting.trees;

public enum Logs { //TODO turn into int[] for item recieved for mushroom shi
    LOGS(1, 1511,25),
    DEAD_TREE(1, 1511,25),
    BURNT_TREE(1, 1511,25),
    EVERGREEN_TREE(1, 1511,25),
    JUNGLE_TREE(1, 1511,25),
    ACHEY_TREE(1, 2862,25),
    OAK_TREE(15, 1521,37.5),
    WILLOW_TREE(30, 1519,67.5),
    TEAK_TREE(35, 6333, 85),
    MATURE_JUNIPER(42, 13355, 35),
    MAPLE_TREE(45, 8177, 100),
    HOLLOW_TREE(45, 3239, 82.5),
    MAHOGANY_TREE(50, 6332, 125),
    ARCTIC_PINE(54, 10810, 40),
    YEW_TREE(60, 8178, 175),
    BLISTERWOOD(62, 24691, 76),
    SULLIUSCEP(65, 21568, 127),
    MAGIC_TREE(75, 8179, 250),
    REDWOOD(90, 19669, 380);

    final int level;
    final int item;
    final double experience;

    Logs(int level, int item, double experience) {
        this.level = level;
        this.item = item;
        this.experience = experience;
    }
}
