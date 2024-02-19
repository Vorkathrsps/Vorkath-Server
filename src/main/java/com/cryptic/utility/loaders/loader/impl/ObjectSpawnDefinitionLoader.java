package com.cryptic.utility.loaders.loader.impl;

import com.cryptic.GameServer;
import com.cryptic.utility.loaders.ObjectSpawnDefinition;
import com.cryptic.utility.loaders.loader.DefinitionLoader;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.google.gson.Gson;

import java.io.FileReader;

public class ObjectSpawnDefinitionLoader extends DefinitionLoader {

    @Override
    public void load() throws Exception {
        try (FileReader reader = new FileReader(file())) {
            ObjectSpawnDefinition[] defs = new Gson().fromJson(reader, ObjectSpawnDefinition[].class);
            for (ObjectSpawnDefinition def : defs) {
                if (!def.isEnabled()) continue;
                ObjectManager.addObj(new GameObject(def.getId(), def.getTile(), def.getType(), def.getFace()));
            }
        }
    }

    @Override
    public String file() {
        return "data/def/objects/object_spawns.json";
    }
}
