package com.aelous.utility.loaders.loader.impl;

import com.aelous.GameServer;
import com.aelous.utility.loaders.ObjectSpawnDefinition;
import com.aelous.utility.loaders.loader.DefinitionLoader;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.google.gson.Gson;

import java.io.FileReader;

public class ObjectSpawnDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Exception {
        try (FileReader reader = new FileReader(file())) {
            ObjectSpawnDefinition[] defs = new Gson().fromJson(reader, ObjectSpawnDefinition[].class);
            for (ObjectSpawnDefinition def : defs) {
                if (!def.isEnabled())
                    continue;
                if (!GameServer.properties().pvpMode && def.PVPWorldExclusive) {
                    //Skip PVP objects in eco world.
                    continue;
                }
                if (GameServer.properties().pvpMode && def.economyExclusive) {
                    //Skip eco objects in PVP world.
                    continue;
                }
                ObjectManager.addObj(new GameObject(def.getId(), def.getTile(), def.getType(), def.getFace()));
            }
        }
    }

    @Override
    public String file() {
        return GameServer.properties().definitionsDirectory + "object_spawns.json";
    }
}
