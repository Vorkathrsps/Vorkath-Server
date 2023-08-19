package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.items.container.presets.PresetData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GsonCreator {
    public static void main(String[] args) {
        List<PresetData> presetList = new ArrayList<>();

        // Create and add presets to the list
       // presetList.add(new PresetData(1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "NORMAL", 123, 456));
       // presetList.add(new PresetData(2, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "LUNAR", 789, 123));

        // Serialize the list to JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(presetList);

        // Write JSON to a file named "presets.json"
        try (FileWriter writer = new FileWriter("presets.json")) {
            writer.write(json);
            System.out.println("Presets JSON written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
