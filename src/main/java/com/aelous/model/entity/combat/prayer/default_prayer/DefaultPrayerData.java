package com.aelous.model.entity.combat.prayer.default_prayer;

import com.aelous.utility.Utils;

import java.util.HashMap;

/**
 * Represents a prayer's configurations, such as their
 * level requirement, buttonId, configId and drain rate.
 *
 * @author relex lawl
 */
public enum DefaultPrayerData {

    THICK_SKIN(1, 5609, 83, 446),
    BURST_OF_STRENGTH(4, 5610, 84, 449),
    CLARITY_OF_THOUGHT(7, 5611, 85, 436),
    SHARP_EYE(8, 19812, 700, -1),
    MYSTIC_WILL(9, 19814, 701, -1),
    ROCK_SKIN(10, 5612, 86, 441),
    SUPERHUMAN_STRENGTH(13, 5613, 87, 434),
    IMPROVED_REFLEXES(16, 5614, 88, 448),
    RAPID_RESTORE(19, 5615, 89, 451),
    RAPID_HEAL(22, 5616, 90, 443),
    PROTECT_ITEM(25, 5617, 91, 433),
    HAWK_EYE(26, 19816, 702, -1),
    MYSTIC_LORE(27, 19818, 703, -1),
    STEEL_SKIN(28, 5618, 92, 439),
    ULTIMATE_STRENGTH(31, 5619, 93, 450),
    INCREDIBLE_REFLEXES(34, 5620, 94, 440),
    PROTECT_FROM_MAGIC(37, 5621, 95, 438, 2),
    PROTECT_FROM_MISSILES(40, 5622, 96, 444, 1),
    PROTECT_FROM_MELEE(43, 5623, 97, 433, 0),
    EAGLE_EYE(44, 19821, 704, -1),
    MYSTIC_MIGHT(45, 19823, 705, -1),
    RETRIBUTION(46, 683, 98, -1, 4),
    REDEMPTION(49, 684, 99, -1, 5),
    SMITE(52, 685, 100, -1, 6),
    PRESERVE(55, 28001, 708, 451),
    CHIVALRY(60, 19825, 706, -1),
    PIETY(70, 19827, 707, -1),
    RIGOUR(74, 28004, 710, -1),
    AUGURY(77, 28007, 712, -1);

    DefaultPrayerData(int requirement, int buttonId, int configId, int soundId, int... hint) {
        this.requirement = requirement;
        this.buttonId = buttonId;
        this.configId = configId;
        this.soundId = soundId;
        if (hint.length > 0)
            this.hint = hint[0];
    }

    public int soundId;

    /**
     * The prayer's level requirement for player to be able
     * to activate it.
     */
    private final int requirement;

    /**
     * The prayer's action button id in prayer tab.
     */
    private final int buttonId;

    /**
     * The prayer's config id to switch their glow on/off by
     * sending the sendConfig packet.
     */
    private final int configId;

    /**
     * The prayer's head icon hint index.
     */
    private int hint = -1;

    public int getRequirement() {
        return requirement;
    }

    public int getButtonId() {
        return buttonId;
    }

    public int getConfigId() {
        return configId;
    }

    public int getHint() {
        return hint;
    }

    /**
     * Gets the prayer's formatted name.
     *
     * @return The prayer's name
     */
    public String getPrayerName() {
        return Utils.capitalizeFirst(toString().toLowerCase().replaceAll("_", " "));
    }

    /**
     * Contains the PrayerData with their corresponding prayerId.
     */
    private static final HashMap<Integer, DefaultPrayerData> prayerData = new HashMap<>();

    public static HashMap<Integer, DefaultPrayerData> getPrayerData() {
        return prayerData;
    }

    /**
     * Contains the PrayerData with their corresponding buttonId.
     */
    private static final HashMap<Integer, DefaultPrayerData> actionButton = new HashMap<>();

    public static HashMap<Integer, DefaultPrayerData> getActionButton() {
        return actionButton;
    }

    /*
     * Populates the prayerId and buttonId maps.
     */
    static {
        for (DefaultPrayerData pd : DefaultPrayerData.values()) {
            prayerData.put(pd.ordinal(), pd);
            actionButton.put(pd.buttonId, pd);
        }
    }
}
