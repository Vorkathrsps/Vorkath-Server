package com.cryptic.model.content.skill.impl.woodcutting.trees;

public enum DyingTree {
    BRONZE(1351, 1, 64, 200),
    IRON(1349, 1,96, 300),
    STEEL(1353, 6, 128, 400),
    BLACK(1361, 11, 144, 450),
    MITHRIL(1355, 21, 160, 500),
    ADAMANT(1357, 31, 192, 600),
    RUNE(1359, 41, 224, 700),
    DRAGON(6739, 61, 240, 750),
    CRYSTAL(23673, 71, 232, 800);
    final int id;
    final int level;
    final int low;
    final int high;

    DyingTree(int id, int level, int low, int high) {
        this.id = id;
        this.level = level;
        this.low = low;
        this.high = high;
    }
}
