package com.aelous.model.entity;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 03, 2020
 */
public enum LockType {

    /**
     * You can't do anything
     */
    FULL,

    /**
     * You can't move but can do other stuff
     */
    MOVEMENT,


    /**
     * Default value
     */
    NONE,

    /**
     * when walking over a plank, or doing an agility obstacle. hits stack up and the last 2 are executed when the event finished
     */
    NULLIFY_DAMAGE,

    /**
     * when you teleport. any incoming damage is not applied at all.
     */
    DELAY_DAMAGE,

    /**
     * same as full, except logout button isnt locked
     */
    FULL_LOGOUT_OK,

    /**
     * Full, but hits are shown as normal
     */
    FULL_WITHDMG,

    MOVEMENT_DAMAGE_OK,
    /**
     * Will stop entity from attacking if this lock is active
     */
    NO_ATTACK
}
