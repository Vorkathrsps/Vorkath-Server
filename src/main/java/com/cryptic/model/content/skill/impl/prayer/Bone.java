package com.cryptic.model.content.skill.impl.prayer;

/**
 * @author Origin | February, 03, 2021, 10:07
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum Bone {
    REGULAR_BONES(526, 4.5, "bones"),
    BURNT_BONES(528, 4.5, "burnt bones"),
    BAT_BONES(530, 4.5, "bat bones"),
    WOLF_BONES(2859, 4.5, "wolf bones"),
    BIG_BONES(532, 15.0, "big bones"),
    LONG_BONE(10976, 15.0, "long bone"),
    CURVED_BONE(10977, 15.0, "curved bone"),
    JOGRE_BONE(3125, 15.0, "jogre bone"),
    BABYDRAGON_BONES(534, 30.0, "baby dragon bone"),
    DRAGON_BONES(536, 72.0, "dragon bones"),
    ZOGRE_BONES(4812, 22.5, "zogre bones"),
    OURG_BONES(4834, 140.0, "ourg bones"),
    WYVERN_BONES(6812, 72.0, "wyvern bones"),
    DAGANNOTH_BONES(6729, 125.0, "dagannoth bones"),
    LAVA_DRAGON_BONES(11943, 85.0, "lava dragon bones"),
    SUPERIOR_DRAGON_BONES(22124, 150.0, "superior dragon bones"),
    WYRM_BONES(22780, 50.0, "wyrm bones"),
    DRAKE_BONES(22783, 80.0, "drake bones"),
    HYDRA_BONES(22786, 110.0, "hydra bones");

    public final int itemId;
    public final double xp;
    public static Bone[] values = values();
    public final String identifier;

    Bone(int itemId, double xp, String identifier) {
        this.itemId = itemId;
        this.xp = xp;
        this.identifier = identifier;
    }

    public static Bone get(int itemId) {
        for (Bone bone : values) {
            if (itemId == bone.itemId) {
                return bone;
            }
        }
        return null;
    }
}
