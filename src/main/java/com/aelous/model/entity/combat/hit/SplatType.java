package com.aelous.model.entity.combat.hit;

/**
 * The enumerated type whose elements represent the hit type of a {@link Splat}.
 *
 * @author Patrick van Elderen | 27 jan. 2019 : 16:39:46
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server
 *      profile</a>}
 */
public enum SplatType {

    BLOCK_HITSPLAT(0),
    HITSPLAT(1),
    POISON_HITSPLAT(2),
    VENOM_HITSPLAT(3),
    DISEASE_HITSPLAT(4),
    NPC_HEALING_HITSPLAT(5),
    VERZIK_SHIELD_HITSPLAT(6),
    SHIELD_ARROW_GREEN_ME(7),
    PLAGUE_SPLAT(8),
    TOA_SHIELD_YELLOW_ME(9),
    TINTED_BLOCK(10),
    TINTED_HIT(11),
    YELLOW_TINTED_UP(12),
    GREEN_TINTED_UP(13),
    GREEN_TINTED_DOWN(14),
    GREEN_TINTED_DOWN2(15),
    PURPLE_DOWN(16),
    PURPLE_DOWN_TINTED(17),
    MAX_HIT(18),
    MAX_HIT_YELLOW_UP(19),
    MAX_HIT_SILVER_DOWN(20),
    MAX_HIT_SHIELD(21),
    MAX_HIT_ARMOR(22),
    MAX_HIT_YELLOW_SHIELD(23),
    MAX_HIT_PURPLE_DOWN(24);

    /**
     * The identification for this hit type.
     */
    private final int id;

    /**
     * Create a new {@link SplatType}.
     *
     * @param id
     *            the identification for this hit type.
     */
    SplatType(int id) {
        this.id = id;
    }

    /**
     * Gets the identification for this hit type.
     *
     * @return the identification for this hit type.
     */
    public final int getId() {
        return id;
    }

}
