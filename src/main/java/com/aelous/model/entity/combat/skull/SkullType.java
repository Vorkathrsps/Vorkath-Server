package com.aelous.model.entity.combat.skull;

import java.util.Optional;

public enum SkullType {

    NO_SKULL(-1),

    WHITE_SKULL(0),

    RED_SKULL(1),
    LOOT_KEY1(2),
    LOOT_KEY2(3),
    LOOT_KEY3(4),
    LOOT_KEY4(5),
    LOOT_KEY5(6);

    private final int cocde;

    SkullType(int code) {
        this.cocde = code;
    }

    public static Optional<SkullType> get(int code) {
        if (code < -1 || code > 1) {
            return Optional.empty();
        }

        return Optional.of(SkullType.values()[code + 1]);
    }

    public int getCode() {
        return cocde;
    }

}
