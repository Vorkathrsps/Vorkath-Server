package com.cryptic.model.entity.combat.method.impl.npcs.bosses.perilsofmoon.bluemoon.state;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;

import java.util.Map;

@Getter
public enum BlueMoonState {
    STAGE_1,
    STAGE_2,
    STAGE_3,
    STAGE_4,
    STAGE_5,
    STAGE_6,
    STAGE_7,
    DOCILE;

    public static final Map<Integer, Integer> INDEX = Map.of(
        STAGE_1.ordinal(), 0,
        STAGE_2.ordinal(), 1,
        STAGE_3.ordinal(), 2,
        STAGE_4.ordinal(), 3,
        STAGE_5.ordinal(), 4,
        STAGE_6.ordinal(), 5,
        STAGE_7.ordinal(), 6
    );

    public BlueMoonState nextStage() {
        return switch (this) {
            case STAGE_1 -> STAGE_2;
            case STAGE_2 -> STAGE_3;
            case STAGE_3 -> STAGE_4;
            case STAGE_4 -> STAGE_5;
            case STAGE_5 -> STAGE_6;
            case STAGE_6 -> STAGE_7;
            default -> STAGE_1;
        };
    }
}
