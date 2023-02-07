package com.aelous.model.entity.combat.method.impl.npcs.bosses.abyssalsire;

/**
 * Created by Situations on 10/12/2016.
 */
public enum AbyssalSireState {

    /**
     * The Abyssal Sire is currently in the middle of switching combat phases.
     */
    SWITCHING_PHASE,

    /**
     * The Abyssal Sire has not yet been awaken, which means he is in a state of stasis.
     */
    STASIS,

    /**
     * The Abyssal Sire has taken 75 damage or has the Shadow effect casted on him.
     */
    DISORIENTED,

    /**
     * The Abyssal Sire is currently engaging in combat with the challenger.
     */
    COMBAT

}
