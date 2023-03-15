package com.aelous.model.entity.combat;

/**
 * A set of constants representing the three different types of combat that can
 * be used.
 *
 * @author lare96
 */
public enum CombatType {

    MELEE,
    RANGED,
    MAGIC;


    public boolean isMelee() {
        return this == MELEE;
    }

    public boolean isRanged() {
        return this == RANGED;
    }

    public boolean isMagic() {
        return this == MAGIC;
    }

}
