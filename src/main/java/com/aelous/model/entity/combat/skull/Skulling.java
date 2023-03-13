package com.aelous.model.entity.combat.skull;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 11, 2020
 * Created by Mack on 10/19/2017.
 */
public class Skulling {

    private static final int SKULL_TIMER_MINS = 20;

    /**
     * If the player meets the criteria to be skulled, we skull them.
     * The skull register timer is 20 mins.
     * <b>Note</b> that the combating parameter is indicating if the skulling action is deriving from a combat
     * context or if we're just giving the player a skull status with no player interaction.
     */
    public static void skull(Player attacker, Entity target, SkullType skullType) {
        if (target == null || attacker.dead() || attacker.getGameMode().isDarklord() || (!target.isPlayer() && target.getAsNpc().getBotHandler() == null)) {
            return;
        }

        if (target.isPlayer()) {
            Player playerTarget = target.getAsPlayer();

            if(attacker.getDueling().inDuel() && playerTarget.getDueling().inDuel()) {
                return;
            }

            CombatFactory.trackPvpAggression(attacker, playerTarget);

            boolean theyHaveAttacked = getHistoryFor(playerTarget).containsKey(attacker.getUsername()); //Means target has recently attacked attacker
            boolean weHaveAttacked = getHistoryFor(attacker).containsKey(playerTarget.getUsername()); //Means attacker has recently attacked target

            if (theyHaveAttacked) {
                checkForRemoval(playerTarget, attacker);
            } else if (weHaveAttacked) {
                checkForRemoval(attacker, playerTarget);
            } else {
                updateAttackHistory(attacker, playerTarget);

                if (!skulled(attacker)) {
                    assignSkullState(attacker, skullType);
                }
            }
        } else {
            if (!skulled(attacker)) {
                assignSkullState(attacker, skullType);
            }
        }
    }

    /**
     * Assigns the skull state to the specified player.
     */
    public static void assignSkullState(Player player, SkullType skullType) {
        player.putAttrib(AttributeKey.SKULL_CYCLES, SKULL_TIMER_MINS);
        player.setSkullType(skullType);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    /**
     * Performs the unskulling action on the specified player.
     */
    public static void unskull(Player player) {
        player.clearAttrib(AttributeKey.SKULL_CYCLES);
        player.clearAttrib(AttributeKey.SKULL_ENTRIES_TRACKER);
        player.setSkullType(SkullType.NO_SKULL);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    public static void unskullNoMapClear(Player player) {
        player.clearAttrib(AttributeKey.SKULL_CYCLES);
        player.setSkullType(SkullType.NO_SKULL);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    /**
     * Decrements the amount of skull cycles remaining.
     */
    public static void decrementSkullCycle(Player player) {
        int skull_cycles = player.getAttribOr(AttributeKey.SKULL_CYCLES, 0);
        if (skull_cycles - 1 <= 0) {
            unskullNoMapClear(player);
        }
        player.modifyNumericalAttribute(AttributeKey.SKULL_CYCLES, -1, 0);
    }

    /**
     * A flag indicating if the player is/should still be considered skulled.
     */
    public static boolean skulled(Player player) {
        int skull_cycles = player.getAttribOr(AttributeKey.SKULL_CYCLES, 0);
        return (skull_cycles > 0);
    }

    /**
     * Updates the specified player's history map.
     */
    private static void updateAttackHistory(Player attacker, Player target) {
        Map<String, Long> map = getHistoryFor(attacker);
        map.put(target.getUsername(), System.currentTimeMillis());
        attacker.putAttrib(AttributeKey.SKULL_ENTRIES_TRACKER, map);
    }

    /**
     * Checks for the removal of the cached id in the map.
     */
    private static void checkForRemoval(Player attacker, Player target) {
        Map<String, Long> map = getHistoryFor(attacker);

        long cachedTime = map.getOrDefault(target.getUsername(), 0L);
        if (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - cachedTime) >= 20) {
            map.remove(target.getUsername());
            updateAttackHistory(attacker, target);
        }
    }

    /**
     * Gets the history for the specified player.
     */
    private static Map<String, Long> getHistoryFor(Player player) {
        return player.getAttribOr(AttributeKey.SKULL_ENTRIES_TRACKER, new HashMap<String, Long>());
    }

}
