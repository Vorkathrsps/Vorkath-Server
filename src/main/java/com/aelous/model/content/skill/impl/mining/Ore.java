package com.aelous.model.content.skill.impl.mining;

import com.aelous.utility.Utils;
import lombok.Getter;

public enum Ore {
    CLAY(new int[]{11363, 11362}, 11390,434, 1, 128, 400, 5.0, 10, 5000, 3727,"clay"),
    COPPER_ROCK(new int[]{10943, 11161}, 11390,436, 1, 128, 400, 17.5, 10, 5000, 3727,"copper ore"),
    TIN_ROCK(new int[]{11361, 11360}, 11390,438, 1, 128, 400, 17.5, 10, 5000, 3727,"tin ore"),
    IRON_ROCK(new int[]{11364, 11365}, 11390,440, 15, 110, 350, 35.0, 10, 4160,3727, "iron ore"),
    SILVER_ROCK(new int[]{11369, 11368}, 11390,442, 20, 25, 200, 40.0, 10, 4000,3727, "silver ore"),
    COAL_ROCK(new int[]{11367, 11366}, 11390,453, 30, 16, 100, 50.0, 10, 3800, 1491,"coal ore"),
    GEM_ROCK(new int[]{}, 11390,Utils.randomElement(new int[]{1}), 40, 84, 210, 65.0, 10, 3900, 1491,"gem ore"),
    GOLD_ROCK(new int[]{11371, 11370}, 11390,444, 40, 6, 75, 65.0, 10, 3500, 1491,"gold ore"),
    MITHRIL(new int[]{11373, 11372}, 11390,447,55, 4, 50, 80.0, 10, 3300, 745,"mithril ore"),
    LOVAKITE(new int[]{}, 13356,11390,65, 4, 25, 10.0, 10, 3200, 745,"lovakite ore"),
    ADAMANT_ROCK(new int[]{11375}, 11390,449, 70, 2, 25, 95.0, 10, 3000, 349,"adamant ore"),
    RUNE_ROCK(new int[]{11377, 11376}, 11390,451, 70, 1, 18, 125.0, 10, 2500, 229,"rune ore"),
    AMETHYST_ROCK(new int[]{11389, 11388}, 11390,21347, 92, -18, 10, 240.0, 10, 2000, 241,"amethyst ore");

    @Getter public final int[] id;
    @Getter public final int replacement_id;
    public final int level_req;
    @Getter public final int low;
    @Getter public final int high;
    @Getter public final double experience;
    public final int item;
    public final int respawn_time;
    public final int pet_chance;
    public final int geode_chance;
    public final String name;

    Ore(int[] id, int replacement_id, int item, int level_req, int low, int high, double experience, int respawn_time, int pet_chance, int geode_chance, String name) {
        this.id = id;
        this.replacement_id = replacement_id;
        this.item = item;
        this.level_req = level_req;
        this.low = low;
        this.high = high;
        this.experience = experience;
        this.respawn_time = respawn_time;
        this.geode_chance = geode_chance;
        this.pet_chance = pet_chance;
        this.name = name;
    }
}
