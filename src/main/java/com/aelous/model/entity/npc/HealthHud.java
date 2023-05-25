package com.aelous.model.entity.npc;

import com.aelous.model.entity.player.Player;
import com.aelous.utility.Varp;
import com.aelous.utility.client_instruction.ClientInstruction;
import lombok.Getter;

/**
 * A class that represents the Health HUD interface.
 *
 * @author Sharky
 * @Since May 23, 2023
 */
public class HealthHud {

    /**
     * The widget id.
     */
    public static final int WIDGET_ID = 19_000;

    /**
     * The varp which controls the type of the hud.
     */
    public static final Varp VARP_TYPE = Varp.createVarp(1312);

    /**
     * The varp which controls the health of the hud.
     */
    public static final Varp VARP = Varp.createVarp(1313);

    /**
     * The type of hud.
     */
    @Getter
    public enum Type {
        REGULAR, ORANGE_SHIELD, CYAN_SHIELD
    }

    /**
     * Opens the hud.
     *
     * @param player The player.
     * @param name   The name of the target.
     * @param health The amount of health.
     */
    public static void open(Player player, Type type, String name, int health) {
        open(player, type, name, health, health);
    }

    /**
     * Opens the hud.
     *
     * @param player        The player.
     * @param name          The name of the target.
     * @param currentHealth The current health.
     * @param maxHealth     The maximum health.
     */
    public static void open(Player player, Type type, String name, int currentHealth, int maxHealth) {
        ClientInstruction instruction = ClientInstruction.of(13);
        instruction.addIntArg(type.ordinal());
        instruction.addStringArg(name);
        instruction.send(player);
        update(player, currentHealth, maxHealth);
        player.getPacketSender().sendParallelInterfaceVisibility(WIDGET_ID, true);
    }

    /**
     * Updates the hud.
     *
     * @param player The player.
     * @param type   The type of the hud.
     * @param health The current amount of health.
     * @param max    The maximum health.
     */
    public static void update(Player player, Type type, int health, int max) {
        VARP_TYPE.set(player, type.ordinal());
        update(player, health, max);
    }

    /**
     * Updates the hud.
     *
     * @param player The player.
     * @param health The current amount of health.
     * @param max    The maximum health.
     */
    public static void update(Player player, int health, int max) {
        VARP.set(player, max << 16 | health);
    }

    /**
     * Closes the hud.
     *
     * @param player The player.
     */
    public static void close(Player player) {
        player.getPacketSender().sendParallelInterfaceVisibility(WIDGET_ID, false);
    }

}
