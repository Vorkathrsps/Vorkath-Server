package com.cryptic.model.items.container.def;

import com.cryptic.model.World;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
public class EquipmentLoader {
    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(World.class);
    public static Int2ObjectMap<EquipmentData> stats = new Int2ObjectOpenHashMap<>();
    public void loadEquipmentDefinitions(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<Int2ObjectOpenHashMap<EquipmentData>>() {}.getType();
            stats = gson.fromJson(reader, linkedData);
            logger.info("Loaded {} Equipment Information", stats.size());
        }
    }
    public EquipmentData getInfo(int id) {
        return stats.get(id);
    }
}

