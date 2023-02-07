package com.aelous.model.entity.combat.hit;

/**
 * The container class that represents a hit.
 * 
 * @author lare96 <http://www.rune-server.org/members/lare96/>
 * @author Patrick van Elderen | 30 jan. 2019 : 13:03:32
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server profile</a>
 */
public final class Splat {

    /**
     * The amount of damage within this hit.
     */
    private final int damage;

    /**
     * The hit type represented by this hit.
     */
    private final SplatType type;

    /**
     * Creates a new {@link Splat}.
     *
     * @param damage the amount of damage within this hit.
     * @param type   the hit type represented by this hit.
     */

    public Splat(int damage, SplatType type) {
        if (damage == 0 && type == SplatType.HITSPLAT) {
            type = SplatType.BLOCK_HITSPLAT;
        } else if (damage > 0 && type == SplatType.BLOCK_HITSPLAT) {
            damage = 0;
        } else if (damage < 0) {
            damage = 0;
        }
        this.damage = damage;
        this.type = type;
    }

    /**
     * Gets the amount of damage within this hit.
     * 
     * @return the amount of damage within this hit.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the hit type represented by this hit.
     * 
     * @return the hit type represented by this hit.
     */
    public SplatType getType() {
        return type;
    }

}
