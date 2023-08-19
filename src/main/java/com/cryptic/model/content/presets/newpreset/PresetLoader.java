package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.presets.PresetData;

public class PresetLoader {
    private Player player;
    private PresetData kits;

    public PresetLoader withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public PresetLoader withKits(PresetData kits) {
        this.kits = kits;
        return this;
    }

    public PresetHandler apply() {
        PresetHandler presetHandler = new PresetHandler();

        return presetHandler;
    }

}
