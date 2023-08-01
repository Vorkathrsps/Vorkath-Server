package com.cryptic.model.content.instance;

/**
 * Holds instance configuration.
 */
public class InstanceConfiguration {

    /**
     * Instance that closes when all players have left and where npcs will not respawn.
     */
    public static final InstanceConfiguration CLOSE_ON_EMPTY = new InstanceConfigurationBuilder()
        .setCloseOnPlayersEmpty(true)
        .createInstanceConfiguration();

    /**
     * Instance that closes when all players have left and where npcs will respawn.
     */
    public static final InstanceConfiguration CLOSE_ON_EMPTY_NO_RESPAWN = new InstanceConfigurationBuilder()
        .setCloseOnPlayersEmpty(true)
        .setRespawnNpcs(false)
        .createInstanceConfiguration();

    /**
     * Will the instance automatically dispose when the
     * player list reaches zero?
     */
    private final boolean closeOnPlayersEmpty;

    /**
     * Will the intsance also npcs to respawn after death?
     * Otherwise the npcs are unregistered from the game.
     */
    private final boolean respawnNpcs;

    private final boolean npcsAreAgro;

    /**
     * Create an {@link InstanceConfiguration}.
     */
    public InstanceConfiguration(boolean closeOnPlayersEmpty, boolean respawnNpcs, boolean npcsAreAgro) {
        this.closeOnPlayersEmpty = closeOnPlayersEmpty;
        this.respawnNpcs = respawnNpcs;
        this.npcsAreAgro = npcsAreAgro;
    }

    @Override
    public String toString() {
        return "InstanceConfiguration{" +
            "closeOnPlayersEmpty=" + closeOnPlayersEmpty +
            ", respawnNpcs=" + respawnNpcs +
            '}';
    }

    public boolean isCloseOnPlayersEmpty() {
        return closeOnPlayersEmpty;
    }

    public boolean isRespawnNpcs() {
        return respawnNpcs;
    }

    public boolean npcsAreAgro() {
        return npcsAreAgro;
    }

}
