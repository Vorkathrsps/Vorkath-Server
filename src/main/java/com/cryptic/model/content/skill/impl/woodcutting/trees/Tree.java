package com.cryptic.model.content.skill.impl.woodcutting.trees;

public enum Tree {
    BRONZE(-1, 1,
        new int[][]
            {
                new int[]{64},
                new int[]{32},
                new int[]{16},
                new int[]{15},
                new int[]{8},
                new int[]{8},
                new int[]{4},
                new int[]{2},
                new int[]{2}
            },
        new int[][]
            {
                new int[]{200},
                new int[]{100},
                new int[]{50},
                new int[]{46},
                new int[]{25},
                new int[]{25},
                new int[]{12},
                new int[]{6},
                new int[]{6}
            }),
    IRON(-1, 1,
        new int[][]
            {
                new int[]{96},
                new int[]{48},
                new int[]{24},
                new int[]{23},
                new int[]{12},
                new int[]{12},
                new int[]{6},
                new int[]{3},
                new int[]{3}
            },
        new int[][]
            {
                new int[]{300},
                new int[]{150},
                new int[]{75},
                new int[]{70},
                new int[]{37},
                new int[]{38},
                new int[]{19},
                new int[]{9},
                new int[]{9}
            }),
    STEEL(-1, 6,
        new int[][]
            {
                new int[]{128},
                new int[]{64},
                new int[]{32},
                new int[]{31},
                new int[]{16},
                new int[]{16},
                new int[]{8},
                new int[]{4},
                new int[]{4}
            },
        new int[][]
            {
                new int[]{400},
                new int[]{200},
                new int[]{100},
                new int[]{93},
                new int[]{50},
                new int[]{50},
                new int[]{25},
                new int[]{12},
                new int[]{12}
            }),
    BLACK(-1, 11,
        new int[][]
            {
                new int[]{144},
                new int[]{72},
                new int[]{36},
                new int[]{35},
                new int[]{18},
                new int[]{18},
                new int[]{9},
                new int[]{5},
                new int[]{4}
            },
        new int[][]
            {
                new int[]{450},
                new int[]{225},
                new int[]{112},
                new int[]{102},
                new int[]{56},
                new int[]{54},
                new int[]{28},
                new int[]{13},
                new int[]{14}
            }),
    MITHRIL(-1, 21,
        new int[][]
            {
                new int[]{160},
                new int[]{80},
                new int[]{40},
                new int[]{39},
                new int[]{20},
                new int[]{20},
                new int[]{10},
                new int[]{5},
                new int[]{5}
            },
        new int[][]
            {
                new int[]{500},
                new int[]{250},
                new int[]{125},
                new int[]{117},
                new int[]{62},
                new int[]{63},
                new int[]{31},
                new int[]{15},
                new int[]{15}
            }),
    ADAMANT(-1, 31,
        new int[][]
            {
                new int[]{192},
                new int[]{96},
                new int[]{48},
                new int[]{47},
                new int[]{24},
                new int[]{25},
                new int[]{12},
                new int[]{6},
                new int[]{6}
            },
        new int[][]
            {
                new int[]{600},
                new int[]{300},
                new int[]{150},
                new int[]{140},
                new int[]{75},
                new int[]{75},
                new int[]{37},
                new int[]{18},
                new int[]{18}
            }),
    RUNE(-1, 41,
        new int[][]
            {
                new int[]{224},
                new int[]{112},
                new int[]{56},
                new int[]{55},
                new int[]{28},
                new int[]{29},
                new int[]{14},
                new int[]{7},
                new int[]{7}
            },
        new int[][]
            {
                new int[]{700},
                new int[]{350},
                new int[]{175},
                new int[]{164},
                new int[]{87},
                new int[]{88},
                new int[]{44},
                new int[]{21},
                new int[]{21}
            }),

    DRAGON(-1, 61,
        new int[][]
            {
                new int[]{240},
                new int[]{120},
                new int[]{60},
                new int[]{60},
                new int[]{30},
                new int[]{34},
                new int[]{15},
                new int[]{7},
                new int[]{7}
            },
        new int[][]
            {
                new int[]{750},
                new int[]{375},
                new int[]{187},
                new int[]{190},
                new int[]{93},
                new int[]{94},
                new int[]{47},
                new int[]{22},
                new int[]{30}
            }),

    CRYSTAL(-1, 71,
        new int[][]
            {
                new int[]{232},
                new int[]{125},
                new int[]{70},
                new int[]{60},
                new int[]{40},
                new int[]{36},
                new int[]{16},
                new int[]{8},
                new int[]{8}
            },
        new int[][]
            {
                new int[]{800},
                new int[]{375},
                new int[]{187},
                new int[]{200},
                new int[]{93},
                new int[]{97},
                new int[]{43},
                new int[]{21},
                new int[]{35}
            });
    final int id;
    final int level;
    final int[][] low;
    final int[][] high;

    Tree(int id, int level, int[][] low, int[][] high) {
        this.id = id;
        this.level = level;
        this.low = low;
        this.high = high;
    }
}
