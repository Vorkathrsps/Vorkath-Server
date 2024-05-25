package com.cryptic.utility.loaders;

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

    public static final BloodMoneyPrices DEFAULT = new BloodMoneyPrices();

    public static BloodMoneyPrices get(int item) {
        return definitions.getOrDefault(item, DEFAULT);
    }

    private int id;
    private int value;

    public int id() {
        return id;
    }

    public int value() {
        return value;
    }
}
