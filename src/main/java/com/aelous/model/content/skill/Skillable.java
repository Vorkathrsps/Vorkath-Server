package com.aelous.model.content.skill;

import com.aelous.model.entity.player.Player;

/**
 * Acts as an interface for a skill which can be
 * trained.
 *
 * @author Professor Oak
 */
public interface Skillable {

    /**
     * Starts the skill.
     * @param player
     */
    void start(Player player);

    /**
     * Cancels the skill.
     * @param player
     */
    void cancel(Player player);

    /**
     * Checks if the player has the requirements to start this skill.
     * @param player
     * @return
     */
    boolean hasRequirements(Player player);

    /**
     * Handles the skill's animation loops.
     * @param player
     */
    void startAnimationLoop(Player player);

    /**
     * The cycles required for the skill to call {@code reward}.
     * Used to determine how long it takes for this skill to execute
     * before the player receives experience/rewards.
     */
    int cyclesRequired(Player player);

    /**
     * This method is called on every cycle.
     */
    void onCycle(Player player);

    /**
     * Once the amount of cycles has hit {@code cyclesRequired},
     * this method will be called. It should be used for rewarding
     * the player with items/experience related to the skill
     */
    void finishedCycle(Player player);
}
