package com.cryptic.model.content.skill.perks;

import com.google.common.collect.ImmutableSortedSet;
import lombok.Getter;

public enum SkillingItems {
    AGILITY_CAPE(new int[]{9771, 9772}, 1.0, 1.0, 1.0),
    RING_OF_ENDURANCE(new int[]{24736}, 1.0, 1.15, 1.0),
    COOKING_GAUNTLETS(new int[]{775}, 1.05, 1.0, 1.0),
    COOKING_CAPE(new int[]{9801, 9802}, 100.0, 1.0, 1.0),
    AMULET_OF_BOUNTY(new int[]{21160}, 1.25, 1.0, 1.0),
    MAGIC_SECATEURS(new int[]{7409}, 1.10, 1.0, 1.0),
    FARMING_CAPE(new int[]{9810, 9811}, 1.05, 1.0, 1.0),
    RADAS_BLESSING(new int[]{22803, 22941, 22943, 22945, 22947}, 8.0, 1.0, 1.0),
    AMULET_OF_GLORY(new int[]{1704, 1706, 1708, 1710, 1712, 11976, 11978, 19707}, 1.05, 1.0, 1.0),
    CELESTIAL_RING(new int[]{25541}, 1.10, 4.0, 1.0),
    MINING_GLOVES(new int[]{21392, 21345, 21343}, 1.05, 1.0, 1.0),
    VARROCK_ARMOUR(new int[]{13107}, 1.10, 1.0, 1.0),
    MINING_CAPE(new int[]{9792, 9793}, 1.10, 1.0, 1.0),
    RUNECRAFTING_CAPE(new int[]{9765, 9766}, 1.0, 1.0, 50.0),
    SMITHING_CAPE(new int[]{9795, 9796}, 1.05, 1.0, 1.0),
    DODGY_NECKLACE(new int[]{21143}, 1.25, 1.0, 1.0),
    GLOVES_OF_SILENCE(new int[]{10075}, 1.05, 1.0, 1.0),
    THIEVING_CAPE(new int[]{9777, 9778}, 1.10, 1.0, 1.0),
    KANDARIN_HELM(new int[]{13140}, 1.0, 1.0, 1.0),
    WOODCUTTING_CAPE(new int[]{9807, 9808}, 1.10, 1.0, 1.0);

    @Getter public final int[] id;
    @Getter public final double chance_increase;
    @Getter public final double boost;
    @Getter public final double extra_yield;

    public static final ImmutableSortedSet<SkillingItems> VALUES = ImmutableSortedSet.copyOf(values()).descendingSet();
    SkillingItems(int[] id, double chance_increase, double boost, double extraYield) {
        this.id = id;
        this.chance_increase = chance_increase;
        this.boost = boost;
        this.extra_yield = extraYield;
    }

}
