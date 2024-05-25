package com.cryptic.model.content.skill.impl.woodcutting.impl;

import com.cryptic.model.items.Item;
import lombok.Getter;

@Getter
public enum Trees {
    TREE("Normal Trees", 1, 1511, 25, new int[]{1276, 1277, 1278, 1279, 1280, 1301, 1303, 1304, 1330, 1331, 1332}, 75, new Item(6020, 2)),
    OAK_TREE("Oak Trees",15, 1521, 37.5, new int[]{4533, 4540, 10820}, 45,new Item(6022,4)),
    WILLOW_TREE("Willow Trees",30, 1519, 67.5, new int[]{4534, 4541, 8481, 8482, 8483, 8484, 8485, 8486, 8487, 8488, 10819, 10831, 10833, 10829}, 30,new Item(6024, 6)),
    TEAK_TREE("Teak Trees",35, 6333, 85, new int[]{30437, 30438, 30439, 30440, 30441, 30442, 30443, 30444, 30445}, 15, null),
    MAPLE_TREE("Maple Trees",45, 1517, 100, new int[]{36681, 36682, 40754, 40755, 4535, 4674, 5126, 8435, 8436, 8437, 8438, 8439, 8440, 8441, 8442, 8443, 8444, 10832}, 25, new Item(6028, 8)),
    MAHOGANY_TREE("Mahogany Trees",50, 6332, 125, new int[]{30407, 30408, 30409, 30410, 30411, 30412, 30413, 30414, 30415, 30416, 30417}, 30, null),
    YEW_TREE("Yew Trees",60, 1515, 175, new int[]{4536, 5121, 8503, 8504, 8505, 8506, 8507, 8508, 8509, 8510, 8511, 8512, 8513, 10822}, 50, new Item(6026, 10)),
    MAGIC_TREE("Magic Trees",75, 1513, 250, new int[]{36685, 4537, 5127, 8396, 8397, 8398, 8399, 8400, 8401, 8402, 8403, 8404, 8405, 8406, 8407, 8408, 8409, 10834}, 75, new Item(6030, 12)),
    REDWOOD("Redwood Trees",90, 19669, 380, new int[]{29668, 29669, 29670, 29671, 34198, 34199, 34205, 34206, 34207, 34208, 34209, 34210, 34211, 34212, 34213, 34214, 34215, 34216, 34217, 34218, 34219, 34220, 34221, 34222, 34223, 34224, 34225, 34226, 34227, 34228, 34229, 34230, 34231, 34232, 34233, 34234, 34235, 34236, 34237, 34238}, 50, null);

    public final String name;
    public final int level;
    public final int item;
    public final double experience;
    public final int[] objects;
    public final int cycle;
    public final Item leaves;
    public static Trees[] values = values();

    Trees(String name, int level, int item, double experience, int[] objects, int cycle, Item leaves) {
        this.name = name;
        this.level = level;
        this.item = item;
        this.experience = experience;
        this.objects = objects;
        this.cycle = cycle;
        this.leaves = leaves;
    }
}
