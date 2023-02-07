package com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah;

/**
 * Created by Bart on 3/6/2016.
 *
 * Marks different forms for Zulrah, since it's a multi-styled boss.
 */
public enum ZulrahForm {

    /**
     * Melee-only phase, using tail to swipe at the player.
     * Can be blocked by being 2 squares away from the snake.
     */
    MELEE(2043),

    /**
     * Magic-only phase, uses toxic fumes and a white orb which summons a snakeling.
     * Only shoots the orbs in areas that have no toxic fumes.
     */
    MAGIC(2044),

    /**
     * Range phase with smoke clouds.
     */
    RANGE(2042),

    /**
     * Phase in which Zulrah can either use magic attacks or range attacks, which need the player
     * to focus on the animation to be able to tell what kind of attack they're getting.
     */
    JAD_RM(2042),

    /**
     * Phase in which Zulrah can either use magic attacks or range attacks, which need the player
     * to focus on the animation to be able to tell what kind of attack they're getting.
     */
    JAD_MR(2042);

    ZulrahForm(int id) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ZulrahForm{" +name()+
            " id=" + id +
            '}';
    }
}
