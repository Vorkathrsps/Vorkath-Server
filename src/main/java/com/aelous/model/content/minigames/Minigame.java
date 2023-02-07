package com.aelous.model.content.minigames;

import com.aelous.core.task.Task;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.model.content.minigames.MinigameManager.MinigameType;
import com.aelous.model.content.minigames.MinigameManager.ItemRestriction;
import com.aelous.model.content.minigames.MinigameManager.ItemType;

/**
 * Represents a minigame
 *
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 *
 */
public abstract class Minigame extends PacketInteraction {

    /**
     * Starts the minigame
     *
     * @param player
     *            the player
     */
    public abstract void start(Player player);

    /**
     * Gets the task
     *
     * @param player
     *            the player
     * @return the task
     */
    public abstract Task getTask(Player player);

    /**
     * Ends the minigame
     *
     * @param player
     *            the player
     */
    public abstract void end(Player player);

    /**
     * Killing in a minigame
     *
     * @param player
     *            the player
     * @param victim
     *            the victim
     */
    public abstract void killed(Player player, Entity victim);

    /**
     * Checks for requirements
     *
     * @param player
     *            the player
     * @return the requirements
     */
    public abstract boolean hasRequirements(Player player);

    /**
     * The item loss policy
     *
     * @return the policy
     */
    public abstract ItemType getType();

    /**
     * The item restriction policy
     *
     * @return the policy
     */
    public abstract ItemRestriction getRestriction();

    /**
     * Gets the combat type
     *
     * @return the combat type
     */
    public abstract MinigameType getMinigameType();

    /**
     * Returns if the player can teleport out
     *
     * @return if can teleport
     */
    public abstract boolean canTeleportOut();

}
