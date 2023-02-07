package com.aelous.model.entity.combat.skull;

import java.util.Optional;

public enum SkullType {
    NO_SKULL(-1),
    WHITE_SKULL(0),
    RED_SKULL(1),
    DARK_LORD_THREE_LIVES(2),
    DARK_LORD_TWO_LIVES(3),
    DARK_LORD_FINAL_LIFE(4);

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
