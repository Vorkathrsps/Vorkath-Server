package com.cryptic.model.content.skill.impl.firemaking.forestry;

public enum Logs {
    LOGS(1511, 10565, 5, 50.5),
    OAK(1521, 10569, 20, 70),
    WILLOW(1519, 10572, 35, 100),
    MAPLE(1517,10568, 50, 175),
    TEAK(6333,10570, 40, 120),
    YEW(1515,10573, 65, 225),
    MAGIC(1513,10566, 80, 404.5),
    REDWOOD(19669,10570, 95, 500);

    final int id;

    final int animation;
    final int level;
    final double experience;

    Logs(int id, int animation, int level, double experience) {
        this.id = id;
        this.animation = animation;
        this.level = level;
        this.experience = experience;
    }
}
