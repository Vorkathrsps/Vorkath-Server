package com.cryptic.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.io.IOException;

public final class MonsterLoader {
    private final ObjectMapper objectMapper;
    public static Int2ObjectOpenHashMap<Monster> monsters;

    public MonsterLoader() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new AfterburnerModule());
    }

    public Int2ObjectOpenHashMap<Monster> loadFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    public static void load() {
        MonsterLoader jsonLoader = new MonsterLoader();
        long start = System.currentTimeMillis();
        try {
            monsters = jsonLoader.loadFromFile("data/def/npcs/npcs.json");
            System.out.println("loaded in " + (System.currentTimeMillis() - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
