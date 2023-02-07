package com.aelous.model.content.skill.impl.smithing;

import com.aelous.model.items.Item;
import com.aelous.model.items.RequiredItem;
import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Represents a bar which can be created by using
 * the required ores with a furnace.
 */
public enum Bar {

    BRONZE_BAR(2349, new RequiredItem[]{new RequiredItem(new Item(TIN_ORE, 1), true), new RequiredItem(new Item(COPPER_ORE, 1), true)}, 1, 6.2, 2405, new int[][]{{3987, 1}, {3986, 5}, {2807, 10}, {2414, -1}}, Optional.of(SmithableEquipment.BRONZE_ITEMS)),
    IRON_BAR(2351, new RequiredItem[]{new RequiredItem(new Item(IRON_ORE, 1), true)}, 15, 12.5, 2406, new int[][]{{3991, 1}, {3990, 5}, {3989, 10}, {3988, -1}}, Optional.of(SmithableEquipment.IRON_ITEMS)),
    SILVER_BAR(2355, new RequiredItem[]{new RequiredItem(new Item(SILVER_ORE, 1), true)}, 20, 13.6, 2407, new int[][]{{3995, 1}, {3994, 5}, {3993, 10}, {3992, -1}}, Optional.empty()),
    STEEL_BAR(2353, new RequiredItem[]{new RequiredItem(new Item(IRON_ORE, 1), true), new RequiredItem(new Item(COAL, 2), true)}, 30, 17.5, 2409, new int[][]{{3999, 1}, {3998, 5}, {3997, 10}, {3996, -1}}, Optional.of(SmithableEquipment.STEEL_ITEMS)),
    GOLD_BAR(2357, new RequiredItem[]{new RequiredItem(new Item(GOLD_ORE, 1), true)}, 40, 22.5, 2410, new int[][]{{4003, 1}, {4002, 5}, {4001, 10}, {4000, -1}}, Optional.empty()),
    MITHRIL_BAR(2359, new RequiredItem[]{new RequiredItem(new Item(MITHRIL_ORE, 1), true), new RequiredItem(new Item(COAL, 4), true)}, 50, 22.5, 2411, new int[][]{{7441, 1}, {7440, 5}, {6397, 10}, {4158, -1}}, Optional.of(SmithableEquipment.MITHRIL_ITEMS)),
    ADAMANTITE_BAR(2361, new RequiredItem[]{new RequiredItem(new Item(ADAMANTITE_ORE, 1), true), new RequiredItem(new Item(COAL, 6), true)}, 70, 37.5, 2412, new int[][]{{7446, 1}, {7444, 5}, {7443, 10}, {7442, -1}}, Optional.of(SmithableEquipment.ADAMANT_ITEMS)),
    RUNITE_BAR(2363, new RequiredItem[]{new RequiredItem(new Item(RUNITE_ORE), true), new RequiredItem(new Item(COAL, 8), true)}, 85, 50, 2413, new int[][]{{7450, 1}, {7449, 5}, {7448, 10}, {7447, -1}}, Optional.of(SmithableEquipment.RUNE_ITEMS)),
    ;

    private final int bar;
    private final RequiredItem[] ores;
    private final int levelReq;
    private final double xpReward;
    private final int frame;
    private final int[][] buttons;
    private final Optional<ImmutableSet<SmithableEquipment>> items;

    Bar(int bar, RequiredItem[] ores, int levelReq, double xpReward, int frame, int[][] buttons, Optional<ImmutableSet<SmithableEquipment>> items) {
        this.bar = bar;
        this.ores = ores;
        this.levelReq = levelReq;
        this.xpReward = xpReward;
        this.frame = frame;
        this.buttons = buttons;
        this.items = items;
    }

    public int getBar() {
        return bar;
    }

    public RequiredItem[] getOres() {
        return ores;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public double getXpReward() {
        return xpReward;
    }

    public int getFrame() {
        return frame;
    }

    public Optional<ImmutableSet<SmithableEquipment>> getItems() {
        return items;
    }

    private static Map<Integer, Bar> smeltables = new HashMap<Integer, Bar>();

    static {
        for (Bar s: Bar.values()) {
            smeltables.put(s.getBar(), s);
        }
    }

    /** Searches for a match for the internal required items. */
    public static Optional<Bar> getDefinitionByItem(int itemId) {
        for (Bar data : Bar.values()) {
            for (RequiredItem item : data.ores) {
                if (item.getItem().getId() == itemId) {
                    return Optional.of(data);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Bar> forBarId(int barId) {
        Bar smeltable = smeltables.get(barId);
        if (smeltable != null) {
            return Optional.of(smeltable);
        }
        return Optional.empty();
    }

    public int[][] getButtons() {
        return buttons;
    }
}
