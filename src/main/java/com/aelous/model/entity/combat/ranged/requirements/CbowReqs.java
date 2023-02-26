package com.aelous.model.entity.combat.ranged.requirements;

import java.util.Arrays;

public enum CbowReqs {

    //CROSSBOWS
    CROSSBOW(9174, "Bronze bolts"),
    BLURITE_CROSSBOW(9174, "Bronze bolts", "Blurite bolts", "Iron bolts"), //877,    9139, 9140
    DORGESHUUN_CBOW(9174, "Bone bolts"),
    BRONZE_CROSSBOW(9174, "Bronze bolts", "Opal bolts"),
    IRON_CROSSBOW(9177, Arrays.deepToString(BRONZE_CROSSBOW.getAmmo()), "Iron bolts"),
    STEEL_CROSSBOW(9179, Arrays.deepToString(IRON_CROSSBOW.getAmmo()), "Steel bolts", "Topaz bolts"),
    MITHRIL_CROSSBOW(9181, Arrays.deepToString(STEEL_CROSSBOW.getAmmo()), "Mithril bolts", "Sapphire bolts"),
    ADAMANT_CROSSBOW(9183, Arrays.deepToString(MITHRIL_CROSSBOW.getAmmo()), "Adamant bolts", "Ruby bolts", "Diamond bolts"),
    RUNE_CROSSBOW(9185, Arrays.deepToString(ADAMANT_CROSSBOW.getAmmo()), "Runite bolts", "Dragonstone bolts"),
    DRAGON_CROSSBOW(21902, Arrays.deepToString(RUNE_CROSSBOW.getAmmo()), "Dragon bolts", "Topaz dragon bolts", "Peal dragon bolts", "Opal dragon bolts", "Sapphire dragon bolts", "Emerald dragon bolts", "Ruby dragon bolts", "Diamond dragon bolts", "Dragonstone dragon bolts"),
    DRAGON_HUNTER_CROSSBOW(21012, DRAGON_CROSSBOW.getAmmo()),
    ARMADYL_CROSSBOW(11785, DRAGON_CROSSBOW.getAmmo()),
    ZARYTE_CROSSBOW(26374, DRAGON_CROSSBOW.getAmmo()),
    DRAGON_HUNTER_CROSSBOW_T(25916, DRAGON_HUNTER_CROSSBOW.getAmmo()),
    HUNTERS_CROSSBOW(10156, "Kebbit bolts"),
    KARIL_CROSSBOW(4734, "Bolt rack"),
    TALONHAWK_CROSSBOW(28641, DRAGON_CROSSBOW.getAmmo());

    private final int bow;
    private final String[] ammo;

    CbowReqs(int bow, String... ammo) {
        this.bow = bow;
        this.ammo = ammo;
    }

    public int getBow() {
        return bow;
    }

    public String[] getAmmo() {
        return ammo;
    }
}
