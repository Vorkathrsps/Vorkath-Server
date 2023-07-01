package com.aelous.model.content.skill.impl.mining;

import com.aelous.utility.Utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import lombok.Getter;

public enum Pickaxe {
    BRONZE(1265, 8, 625, 1),
    IRON(1267,7,626,1),
    STEEL(1269, 6,627,6),
    BLACK(12297,5,3866,11),
    MITHRIL(1273,5,629,21),
    ADAMANT(1271,4,628,31),
    RUNE(1275,3,624,41),
    GILDED(23276,3,624,41),
    DRAGON(11920, Utils.random(2, 3),7139,61),
    THIRD_AGE(20014,Utils.random(2, 3),7283,61),
    INFERNAL(13243, Utils.random(2,3), 4482, 61),
    CRYSTAL_PICKAXE(23680, Utils.random(2,3), 8329, 71);

    @Getter public final int id;
    public final int anim;
    public final int level;
    @Getter public final int delay;

    Pickaxe(int id, int delay, int anim, int level) {
        this.id = id;
        this.delay = delay;
        this.anim = anim;
        this.level = level;
    }

    public static final ImmutableSortedSet<Pickaxe> VALUES = ImmutableSortedSet.copyOf(values()).descendingSet();

}
