package com.aelous.utility.loaders;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the blood money value for certain items.
 * These values specifically apply to the PVP world.
 *
 * @author PVE | Zerikoth
 */
public class BloodMoneyPrices {

    public static final Map<Integer, BloodMoneyPrices> definitions = new HashMap<>();

    private final int id;
    private final int value;

    public BloodMoneyPrices(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int id() {
        return id;
    }

    public int value() {
        return value;
    }
}
