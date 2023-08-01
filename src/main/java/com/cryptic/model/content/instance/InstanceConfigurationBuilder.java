package com.cryptic.model.content.instance;

public class InstanceConfigurationBuilder {

    /**
     * see {@link InstanceConfiguration#isCloseOnPlayersEmpty()}
     */
    private boolean closeOnPlayersEmpty;
    /**
     * see {@link InstanceConfiguration#isRespawnNpcs()}
     */
    private boolean respawnNpcs;
    /**
     * see {@link InstanceConfiguration#npcsAreAgro()}
     */
    private boolean npcsAreAgro;


    public InstanceConfigurationBuilder setCloseOnPlayersEmpty(boolean closeOnPlayersEmpty) {
        this.closeOnPlayersEmpty = closeOnPlayersEmpty;
        return this;
    }

    public InstanceConfigurationBuilder setRespawnNpcs(boolean respawnNpcs) {
        this.respawnNpcs = respawnNpcs;
        return this;
    }

    public InstanceConfigurationBuilder setNpcsAgro(boolean isAgro) {
        this.npcsAreAgro = isAgro;
        return this;
    }

    public InstanceConfiguration createInstanceConfiguration() {
        return new InstanceConfiguration(closeOnPlayersEmpty, respawnNpcs, npcsAreAgro);
    }
}
