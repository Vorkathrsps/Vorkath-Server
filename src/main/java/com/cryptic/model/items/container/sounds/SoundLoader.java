package com.cryptic.model.items.container.sounds;

import com.cryptic.model.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class SoundLoader implements Runnable {
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(World.class);
    public static Object2ObjectLinkedOpenHashMap<Integer, SoundData> soundMap = new Object2ObjectLinkedOpenHashMap<>();

    public void loadSounds(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<Object2ObjectLinkedOpenHashMap<Integer, SoundData>>() {
            }.getType();
            soundMap = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Linked Sound Information", soundMap.size());
        }
    }

    public SoundData getInfo(int id) {
        return soundMap.get(id);
    }

    @Override
    public void run() {
        try {
            loadSounds(new File("data/list/sounds.json"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
