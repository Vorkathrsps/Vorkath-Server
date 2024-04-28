package com.cryptic.model.content.sound;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class SoundDataLoader {
    public static final Object2IntMap<String> sounds = new Object2IntOpenHashMap<>();

    public static void loadDataFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int id = Integer.parseInt(parts[1].trim());
                    sounds.put(name, id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Integer getIdByName(String name) {
        return sounds.getOrDefault(name, -1);
    }

    public static String getNameById(int id) {
        for (Map.Entry<String, Integer> entry : sounds.object2IntEntrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }
        return null; // If ID not found
    }
}
