package com.cryptic.utility;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.items.container.presets.AttributeKeyTypeAdapter;
import com.cryptic.model.items.container.presets.SpellBookTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author PVE
 * @Since augustus 15, 2020
 */
public class JGson {

    public static Gson get() {
        return new GsonBuilder().registerTypeAdapterFactory(new GsonPropertyValidator())
            .setPrettyPrinting().create();
    }

    public static Gson buildTypeAdapter() {
        return new GsonBuilder()
            .registerTypeAdapterFactory(new GsonPropertyValidator())
            .registerTypeAdapter(MagicSpellbook.class, new SpellBookTypeAdapter())
            .registerTypeAdapter(AttributeKey.class, new AttributeKeyTypeAdapter())
            .create();
    }
}
