package com.cryptic.model.entity.combat.prayer.default_prayer;

import lombok.Getter;

import java.util.HashMap;

@Getter
public enum PrayerData {
    THICK_SKIN(0, 83,1, 3, 5609),
    BURST_OF_STRENGTH(1, 84,4, 3,5610),
    CLARITY_OF_THOUGHT(2, 85,7, 3,5611),
    SHARP_EYE(3, 700,8, 3,19812),
    MYSTIC_WILL(4, 701,9, 3,19814),
    ROCK_SKIN(5, 86,10, 6,5612),
    SUPERHUMAN_STRENGTH(6, 87,13, 6,5613),
    IMPROVED_REFLEXES(7, 88,16, 6,5614),
    RAPID_RESTORE(8, 89,19, 1,5615),
    RAPID_HEAL(9, 90,22, 2,5616),
    PROTECT_ITEM(10, 91,25, 2,5617),
    HAWK_EYE(11, 702,26, 6,19816),
    MYSTIC_LORE(12, 703,27, 6,19818),
    STEEL_SKIN(13, 92,28, 12,5618),
    ULTIMATE_STRENGTH(14, 93,31, 12,5619),
    INCREDIBLE_REFLEXES(15, 94,34, 12,5620),
    PROTECT_FROM_MAGIC(16, 95,37, 12,5621),
    PROTECT_FROM_MISSILES(17, 96,40, 12,5622),
    PROTECT_FROM_MELEE(18, 97,43, 12,5623),
    EAGLE_EYE(19, 704,44, 12,19821),
    MYSTIC_MIGHT(20, 705,45, 12,19823),
    RETRIBUTION(21, 98,46, 3,683),
    REDEMPTION(22, 99,49, 6,684),
    SMITE(23, 100,52, 18,685),
    PRESERVE(24, 708,55, 2,28001),

    CHIVALRY(25, 706,60, 24,19825),
    PIETY(26, 707,70, 24,19827),
    RIGOUR(27, 710,74, 24,28004),
    AUGURY(28, 712,77, 24,28007);

    public final int prayer_id;
    public final int config_id;
    public final int button_id;
    public final int level;
    public final int drainRate;
    public int hint = -1;

    PrayerData(final int prayer_id, final int config_id, final int level, final int drainRate, final int button_id) {
        this.prayer_id = prayer_id;
        this.config_id = config_id;
        this.level = level;
        this.drainRate = drainRate;
        this.button_id = button_id;
    }

    /**
     * Contains the PrayerData with their corresponding prayerId.
     */
    private static final HashMap<Integer, PrayerData> prayerData = new HashMap<>();

    public static HashMap<Integer, PrayerData> getPrayerData() {
        return prayerData;
    }

    /**
     * Contains the PrayerData with their corresponding buttonId.
     */
    private static final HashMap<Integer, PrayerData> actionButton = new HashMap<>();

    public static HashMap<Integer, PrayerData> getActionButton() {
        return actionButton;
    }

    /*
     * Populates the prayerId and buttonId maps.
     */
    static {
        for (PrayerData pd : PrayerData.values()) {
            prayerData.put(pd.ordinal(), pd);
            actionButton.put(pd.getButton_id(), pd);
        }
    }
}
