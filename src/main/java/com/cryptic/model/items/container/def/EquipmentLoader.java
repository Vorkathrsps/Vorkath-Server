package com.cryptic.model.items.container.def;

import com.cryptic.model.World;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EquipmentLoader {
    private static final Logger logger = LogManager.getLogger(World.class);
    public static Int2ObjectOpenHashMap<EquipmentData> equipment = new Int2ObjectOpenHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new AfterburnerModule());

    public Int2ObjectOpenHashMap<EquipmentData> loadFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }

    public void loadEquipmentDefinitions(File file) throws IOException {
        equipment = loadFromFile(file.getAbsolutePath());
        logger.info("Loaded {} Equipment Information", equipment.size());
    }

    public EquipmentData getInfo(int id) {
        return equipment.get(id);
    }
}

