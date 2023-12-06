package com.cryptic.model.items.container.sounds;

import com.cryptic.model.World;
import com.cryptic.model.items.container.sounds.data.SpellSounds;
import com.cryptic.model.items.container.sounds.data.WeaponSounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class SoundLoader implements Runnable {
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(World.class);
    public static Object2ObjectLinkedOpenHashMap<Integer, WeaponSounds> weapon_sounds = new Object2ObjectLinkedOpenHashMap<>();

    public void loadWeaponSounds(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<Object2ObjectLinkedOpenHashMap<Integer, WeaponSounds>>() {
            }.getType();
            weapon_sounds = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Linked Sound Information", weapon_sounds.size());
        }
    }

    public static Object2ObjectLinkedOpenHashMap<Integer, SpellSounds> spell_sounds = new Object2ObjectLinkedOpenHashMap<>();

    public void loadSpellSounds(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<Object2ObjectLinkedOpenHashMap<Integer, SpellSounds>>() {
            }.getType();
            spell_sounds = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Linked Sound Information", spell_sounds.size());
        }
    }

    public WeaponSounds getInfo(int id) {
        return weapon_sounds.get(id);
    }

    public SpellSounds getSpellInfo(int id) {
        return spell_sounds.get(id);
    }

    @Override
    public void run() {
        try {
            loadWeaponSounds(new File("data/list/weapon_sounds.json"));
            loadSpellSounds(new File("data/list/spell_sounds.json"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
