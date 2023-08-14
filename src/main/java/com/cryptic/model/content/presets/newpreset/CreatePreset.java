package com.cryptic.model.content.presets.newpreset;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatePreset {
    String name;
    List<Item>  inventoryItems;
    List<Item> equipmentItems;
    List<Skill> savedSkills;
    String saveDirectory = "C:\\Users\\2007z\\OneDrive\\Desktop\\Aelous210\\data\\saves\\characters\\";

    public CreatePreset(String name, List<Item> inventoryItems, List<Item> equipmentItems, List<Skill> savedSkills) {
        this.name = name;
        this.inventoryItems = inventoryItems;
        this.equipmentItems = equipmentItems;
        this.savedSkills = savedSkills;
    }

    void appendToJsonFile(CreatePreset preset, Player player) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Path filePath = Path.of(saveDirectory + player.getUsername() + ".json");

        try {
            JsonObject jsonObject;
            if (Files.exists(filePath)) {
                try (FileReader reader = new FileReader(filePath.toFile())) {
                    jsonObject = gson.fromJson(reader, JsonObject.class);
                }
            } else {
                jsonObject = new JsonObject();
                jsonObject.add("presets", new JsonArray());
            }

            JsonArray presetsArray = jsonObject.getAsJsonArray("presets");

            // Clear the existing presetsList
            List<CreatePreset> presetsList = new ArrayList<>();

            // Add the new preset to the cleared presetsList
            presetsList.add(preset);

            // Convert the List back to a JsonArray
            JsonArray updatedPresetsArray = new JsonArray();
            for (CreatePreset existingPreset : presetsList) {
                updatedPresetsArray.add(gson.toJsonTree(existingPreset));
            }

            jsonObject.add("presets", updatedPresetsArray);

            // Write the updated JsonArray directly to the file
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(gson.toJson(jsonObject));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    void writeToJsonFile(CreatePreset preset, Player player) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(player.getUsername() + ".json")) {
            gson.toJson(Collections.singletonList(preset), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
