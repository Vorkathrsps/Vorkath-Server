package com.cryptic.model.content.skill.impl.woodcutting.impl;

import lombok.Getter;

public enum Trees {
    LOGS(1, 1511, 25, new int[]{1276, 1277, 1278, 1279, 1280, 1301, 1303, 1304, 1330, 1331, 1332}, 75),
    OAK_TREE(15, 1521, 37.5, new int[]{4533, 4540, 10820}, 45),
    WILLOW_TREE(30, 1519, 67.5, new int[]{4534, 4541, 8481, 8482, 8483, 8484, 8485, 8486, 8487, 8488, 10819, 10831}, 30),
    TEAK_TREE(35, 6333, 85, new int[]{30437, 30438, 30439, 30440, 30441, 30442, 30443, 30444, 30445}, 15),
    MAPLE_TREE(45, 8177, 100, new int[]{36681, 36682, 40754, 40755, 4535, 4674, 5126, 8435, 8436, 8437, 8438, 8439, 8440, 8441, 8442, 8443, 8444, 10832}, 25),
    MAHOGANY_TREE(50, 6332, 125, new int[]{30407, 30408, 30409, 30410, 30411, 30412, 30413, 30414, 30415, 30416, 30417}, 30),
    YEW_TREE(60, 1515, 175, new int[]{4536, 5121, 8503, 8504, 8505, 8506, 8507, 8508, 8509, 8510, 8511, 8512, 8513, 10822}, 50),
    MAGIC_TREE(75, 1513, 250, new int[]{36685, 4537, 5127, 8396, 8397, 8398, 8399, 8400, 8401, 8402, 8403, 8404, 8405, 8406, 8407, 8408, 8409, 10834}, 75),
    REDWOOD(90, 19669, 380, new int[]{29668, 29669, 29670, 29671, 34198, 34199, 34205, 34206, 34207, 34208, 34209, 34210, 34211, 34212, 34213, 34214, 34215, 34216, 34217, 34218, 34219, 34220, 34221, 34222, 34223, 34224, 34225, 34226, 34227, 34228, 34229, 34230, 34231, 34232, 34233, 34234, 34235, 34236, 34237, 34238}, 50);

    @Getter public final int level;
    @Getter public final int item;
    @Getter public final double experience;
    @Getter public final int[] objects;
    @Getter public final int cycle;

    Trees(int level, int item, double experience, int[] objects, int cycle) {
        this.level = level;
        this.item = item;
        this.experience = experience;
        this.objects = objects;
        this.cycle = cycle;
    }
}
