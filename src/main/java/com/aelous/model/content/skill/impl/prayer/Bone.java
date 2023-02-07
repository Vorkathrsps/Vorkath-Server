package com.aelous.model.content.skill.impl.prayer;

/**
 * @author Patrick van Elderen | February, 03, 2021, 10:07
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum Bone {
    REGULAR_BONES(526, 4.5),
    BURNT_BONES(528, 4.5),
    BAT_BONES(530, 4.5),
    WOLF_BONES(2859, 4.5),
    BIG_BONES(532, 15.0),
    LONG_BONE(10976, 15.0),
    CURVED_BONE(10977, 15.0),
    JOGRE_BONE(3125, 15.0),
    BABYDRAGON_BONES(534, 30.0),
    DRAGON_BONES(536, 72.0),
    ZOGRE_BONES(4812, 22.5),
    OURG_BONES(4834, 140.0),
    WYVERN_BONES(6812, 72.0),
    DAGANNOTH_BONES(6729, 125.0),
    LAVA_DRAGON_BONES(11943, 85.0),
    SUPERIOR_DRAGON_BONES(22124, 150.0),
    ENCHANTED_BONES(30042, 175.0),
    WYRM_BONES(22780, 50.0),
    DRAKE_BONES(22783, 80.0),
    HYDRA_BONES(22786, 110.0);

    public final int itemId;
    public final double xp;

    Bone(int itemId, double xp) {
        this.itemId = itemId;
        this.xp = xp;
    }

    public static Bone get(int itemId) {
        for (Bone bone : Bone.values()) {
            if (itemId == bone.itemId) {
                return bone;
            }
        }
        return null;
    }
}
