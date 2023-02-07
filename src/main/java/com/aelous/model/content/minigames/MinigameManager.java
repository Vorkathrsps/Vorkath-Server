package com.aelous.model.content.minigames;

import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles minigames
 *
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>
 */
public class MinigameManager {


    /**
     * The minigame place
     */
    public static final Tile MINIGAME = new Tile(0, 0, 0);

    /**
     * The minigame value attribute
     */
    private AttributeKey value;

    /**
     * The minigame state attribute
     */
    private Map<String, Boolean> state = new HashMap<>();

    /**
     * The values stored
     */
    public static final String[] VALUES_STORED = {  };

    /**
     * The states stored
     */
    public static final String[] STATES_STORED = {  };

    /**
     * Minigame cycle
     */
    public static void onTick() {

    }

    /**
     * The minigame type
     */
    public enum MinigameType {
        /**
         * Safe zone, no combat
         */
        SAFE,
        /**
         * Safe zone, but multi
         */
        SAFE_MULTI,
        /**
         * Dangerous 1v1
         */
        DANGEROUS,
        /**
         * Dangerous multi combat
         */
        DANGEROUS_MULTI

    }

    /**
     * The minigame type
     */
    public enum ItemType {

        /**
         * The safe minigame
         */
        SAFE,
        /**
         * Dangerous minigame
         */
        DANGEROUS

    }

    /**
     * The minigame item restriction
     */
    public enum ItemRestriction {
        /**
         * No items allowed
         */
        NO_ITEMS,
        /**
         * No inventory items allowed
         */
        NO_INVENTORY,
        /**
         * No equipment allowed
         */
        NO_EQUIPMENT,
        /**
         * No restriction
         */
        NONE

    }

    /**
     * Playing a minigame
     *
     * @param player
     *            the player
     * @param minigame
     *            the minigame
     */
    public static void playMinigame(Player player, Minigame minigame) {
        // Already in a minigame
        if (player.getMinigame() != null) {
            return;
        }

        // Checks requirements
        if (!minigame.hasRequirements(player)) {
            return;
        }

        // Checks item restrictions
        switch (minigame.getRestriction()) {
            case NO_EQUIPMENT:
                if (!player.getEquipment().isEmpty()) {
                    player.message("You aren't allowed to bring any equipment in this minigame.");
                    return;
                }
                break;
            case NO_INVENTORY:
                if (!player.inventory().isEmpty()) {
                    player.message("You aren't allowed to bring any inventory items in this minigame.");
                    return;
                }
                break;
            case NO_ITEMS:
                if (!player.inventory().isEmpty()) {
                    player.message("You aren't allowed to bring any inventory items in this minigame.");
                    return;
                }
                if (!player.getEquipment().isEmpty()) {
                    player.message("You aren't allowed to bring any equipment in this minigame.");
                    return;
                }
                break;
            default:
                break;
        }

        if(player.isNullifyDamageLock()) {
            return;
        }

        // Sets minigame
        player.setMinigame(minigame);
        // Starts the minigame
        minigame.start(player);
        // Starts the task
        if (minigame.getTask(player) != null) {
            TaskManager.submit(minigame.getTask(player));
        }
    }

    /**
     * Sets the attributes
     *
     * @return the attributes
     */
    public AttributeKey getValue() {
        return value;
    }

    /**
     * Sets the attributes
     *
     * @param attributes
     *            the attributes
     */
    public void setValue(AttributeKey attributes) {
        this.value = attributes;
    }

    /**
     * Gets the state
     *
     * @return the state
     */
    public Map<String, Boolean> getState() {
        return state;
    }

    /**
     * Sets the state
     *
     * @param state
     *            the state
     */
    public void setState(Map<String, Boolean> state) {
        this.state = state;
    }

}
