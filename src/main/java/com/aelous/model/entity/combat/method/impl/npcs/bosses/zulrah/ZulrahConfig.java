package com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah;

/**
 * Created by Bart on 3/30/2016.
 *
 * Specific attributes that revolve around the combat scene.
 */
public enum ZulrahConfig {

    /**
     * During this phase, Zulrah does not attack the player. Generally only does fumes or snakelings.
     */
    NO_ATTACK,

    /**
     * The entire area is filled with smoke fumes.
     */
    FULL_TOXIC_FUMES,

    /**
     * Zulrah releases two snakelings, four toxic fumes south and then two more snakelings before vanishing.
     */
    SNAKELINGS_CLOUDS_SNAKELINGS,

    /**
     * Zulrah begins by releasing toxic fumes everywhere but east, and releases 4 snakelings east.
     */
    EAST_SNAKELINGS_REST_FUMES,

    /**
     * Zulrah releases a snakeling southwest (or at player?), two fumes south, a snakeling northwest,
     * two fumes east, and another snakeling west centered.
     */
    SNAKELING_FUME_MIX;

    @Override
    public String toString() {
        return name();
    }
}
