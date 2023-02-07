package com.aelous.utility.loaders.loader.impl;


import com.aelous.GameServer;
import com.aelous.model.content.presets.PresetManager;
import com.aelous.model.content.presets.Presetable;
import com.aelous.utility.loaders.loader.DefinitionLoader;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.items.Item;
import com.google.gson.*;

import java.io.FileReader;
import java.security.InvalidParameterException;

public class PresetLoader extends DefinitionLoader {

    public static int PRESETS_LOADED;

    @Override
    public void load() throws Exception {
        try (FileReader in = new FileReader(file())) {
            JsonArray array = (JsonArray) JsonParser.parseReader(in);
            Gson builder = new GsonBuilder().create();
            for (int i = 0; i < array.size(); i++) {
                // System.out.println("Loading preset " + i);
                JsonObject reader = (JsonObject) array.get(i);
                String name = reader.get("name").getAsString();
                String spellbook = reader.get("spellbook").getAsString();
                int[] stats = builder.fromJson(reader.get("stats").getAsJsonArray(), int[].class);
                Item[] inventory = builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class);
                Item[] equipment = builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class);
                Item[] runePouch = reader.has("runepouch") ? builder.fromJson(reader.get("runepouch").getAsJsonArray(), Item[].class) : new Item[0];

                if(inventory.length > 29) {
                    throw new InvalidParameterException("Preset "+name+" has too many inventory items. Max is 28!");
                }

                if(equipment.length > 14) {
                    throw new InvalidParameterException("Preset "+name+" has too many equipment items. Max is 14!");
                }

                PresetManager.GLOBAL_PRESETS[PRESETS_LOADED] = new Presetable(name, PRESETS_LOADED, inventory, equipment, stats, MagicSpellbook.valueOf(spellbook), true, runePouch);
                PRESETS_LOADED++;
            }
        }
    }

    @Override
    public String file() {
        return GameServer.properties().definitionsDirectory + "presets.json";
    }
}
