package com.cryptic.model.content.skill.impl.woodcutting.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import lombok.Getter;

import java.util.Optional;

public enum Axe {
    BRONZE(1351, 879, 1,
        new int[][]
            {
                {64, 200},
                {32, 100},
                {16, 50},
                {15, 46},
                {8, 25},
                {8, 25},
                {4, 12},
                {2, 6},
                {2, 6}
            }),
    IRON(1349, 877,1,
        new int[][]
            {
                {96, 300},
                {48, 150},
                {24, 75},
                {23, 70},
                {12, 37},
                {12, 38},
                {6, 19},
                {3, 9},
                {3, 9}
            }),
    STEEL(1353, 875,6,
        new int[][]
            {
                {128, 400},
                {64, 200},
                {32, 100},
                {31, 93},
                {16, 50},
                {16, 50},
                {8, 25},
                {4, 12},
                {4, 12}
            }),
    BLACK(1361, 873,11,
        new int[][]
            {
                {144, 450},
                {72, 225},
                {36, 112},
                {35, 102},
                {18, 56},
                {18, 54},
                {9, 28},
                {5, 13},
                {4, 14}
            }),
    MITHRIL(1355, 871,21,
        new int[][]
            {
                {160, 500},
                {80, 250},
                {40, 125},
                {39, 117},
                {20, 62},
                {20, 63},
                {10, 31},
                {5, 15},
                {5, 15}
            }),
    ADAMANT(1357, 869,31,
        new int[][]
            {
                {192, 600},
                {96, 300},
                {48, 150},
                {47, 140},
                {24, 75},
                {25, 75},
                {12, 37},
                {6, 18},
                {6, 18}
            }),
    RUNE(1359, 867,41,
        new int[][]
            {
                {224, 700},
                {112, 350},
                {56, 175},
                {55, 164},
                {28, 87},
                {29, 88},
                {14, 44},
                {7, 21},
                {7, 21}
            }),
    DRAGON(6739, 2846,61,
        new int[][]
            {
                {240, 750},
                {120, 350},
                {60, 187},
                {60, 190},
                {30, 93},
                {34, 94},
                {15, 47},
                {7, 22},
                {7, 30}
            }),
    INFERNAL(13241, 2117,61,
        new int[][]
            {
                {240, 750},
                {120, 350},
                {60, 187},
                {60, 190},
                {30, 93},
                {34, 94},
                {15, 47},
                {7, 22},
                {7, 30}
            }),
    CRYSTAL(23862, 8324,71,
        new int[][]
            {
                {232, 800},
                {125, 375},
                {70, 187},
                {60, 200},
                {40, 93},
                {36, 97},
                {16, 43},
                {8, 21},
                {8, 35}
            });

    @Getter public final int id;
    @Getter public final int anim;
    @Getter public final int level;
    @Getter public final int[][] values;

    Axe(int id, int anim, int level, int[][] values) {
        this.id = id;
        this.anim = anim;
        this.level = level;
        this.values = values;
    }

    public static final ImmutableSet<Axe> VALUES = ImmutableSortedSet.copyOf(values()).descendingSet();

    public static Optional<Axe> findAxe(Player player) {
        if (player.getEquipment().hasWeapon()) {
            Optional<Axe> result = Axe.VALUES.stream().filter(it -> player.getEquipment().contains(it.id) && player.getSkills().levels()[Skills.WOODCUTTING] >= it.level).findFirst();

            if (result.isPresent()) {
                return result;
            }
        }
        return Axe.VALUES.stream().filter(def -> player.inventory().contains(def.id) && player.getSkills().levels()[Skills.WOODCUTTING] >= def.level).findAny();
    }

}

