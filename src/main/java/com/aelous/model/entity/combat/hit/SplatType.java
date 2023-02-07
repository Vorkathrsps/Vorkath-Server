package com.aelous.model.entity.combat.hit;

/**
 * The enumerated type whose elements represent the hit type of a {@link Splat}.
 *
 * @author Patrick van Elderen | 27 jan. 2019 : 16:39:46
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server
 *      profile</a>}
 */
public enum SplatType {

    /**
     * A hit of zero, inflicts no damage. It usually indicates either a
     * missed/blocked attack, but can also rarely be a successful hit.
     */
    BLOCK_HITSPLAT(0, -1),

    /**
     * Regular damage from Melee, Magic, Ranged, Agility obstacles, Thieving
     * traps, etc.
     */
    HITSPLAT(1, -1),

    /**
     * Poison, damages you from time to time (decreases over time).
     */
    POISON_HITSPLAT(2, -1),

    /**
     * Disease, drains stats.
     */
    DISEASE_HITSPLAT(3, 4),

    /**
     * Venom, damages you from time to time (increases over time).
     */
    VENOM_HITSPLAT(5, -1),

    /**
     * Used to represent an NPC healing its hitpoints. Currently used during the
     * Vorkath, Grotesque Guardians, rune dragons, the Great Olm and Ranis
     * Drakan fights.
     */
    NPC_HEALING_HITSPLAT(6, -1),

    /**
     * Damage dealt to Verzik's Shield. Currently used exclusively in the first
     * phase of the fight with Verzik Vitur.
     */
    VERZIK_SHIELD_HITSPLAT(7, -1),

    /**
     * The Hitsplats sent when the combat type is null/player not in combat
     */

    GREY_BLUE(8, -1),


    GREY_RED(9, -1);

    /**
     * The identification for this hit type.
     */
    private final int id;

    /**
     * The secondary identification for this hit type.
     */
    private final int secondary_id;

    /**
     * Create a new {@link SplatType}.
     *
     * @param id
     *            the identification for this hit type.
     *
     * @param secondary_id
     *            the secondary identification for this hit type.
     */
    SplatType(int id, int secondary_id) {
        this.id = id;
        this.secondary_id = secondary_id;
    }

    /**
     * Gets the identification for this hit type.
     *
     * @return the identification for this hit type.
     */
    public final int getId() {
        return id;
    }

    /**
     * Gets the secondary identification for this hit type.
     *
     * @return the secondary identification for this hit type.
     */
    public final int getSecondaryId() {
        return secondary_id;
    }
}
