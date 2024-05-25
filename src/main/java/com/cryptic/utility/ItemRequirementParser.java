package com.cryptic.utility;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.items.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

public class ItemRequirementParser {

    public static Int2ObjectMap<String> itemNames = new Int2ObjectOpenHashMap<>();

    public static void main(String[] args) {
        try {
            // Create a map to store the data
            Map<Integer, Map<Integer, Integer>> requirementMap = new HashMap<>();

            // Read the text file
            Scanner scanner = new Scanner(new File(Path.of("data", "combat", "weapons", "requirements.txt").toUri()));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    int itemId = Integer.parseInt(parts[0].trim());
                    String[] keyValue = parts[1].split("=");
                    if (keyValue.length == 2) {
                        int skillId = Integer.parseInt(keyValue[0].trim());
                        int requirementLevel = Integer.parseInt(keyValue[1].trim());
                        Map<Integer, Integer> skillMap = requirementMap.getOrDefault(itemId, new HashMap<>());
                        skillMap.put(skillId, requirementLevel);
                        requirementMap.put(itemId, skillMap);
                    }
                }
            }
            scanner.close();


            loadItems();


            List<RequirementDef> requirementDefs = getRequirementDefs(requirementMap);

            JSONArray jsonArray = new JSONArray();
            for (RequirementDef requirementDef : requirementDefs) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", requirementDef.name);
                jsonObject.put("requirement", requirementDef.requirement);
                jsonObject.put("skill", requirementDef.skill.getName()); // Convert skill ID to its name
                jsonArray.add(jsonObject);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("requirements.json"));
            writer.write(jsonArray.toJSONString());
            writer.close();


            System.out.println("Parsing completed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadItems() {
        for (Field field : ItemIdentifiers.class.getFields()) {
            try {
                String name = field.getName();
                int id = field.getInt(ItemIdentifiers.class);
                itemNames.put(id, name);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getItemName(int id) {
        if (id <= 0) return "null";
        var def = ItemDefinition.cached.get(id);
        String itemName;
        if (def != null && def.noted()) {
            var item = new Item(def.id);
            var notedID = item.note().getId();
            itemName = "NOTED_" + item.unnote().name().replaceAll(" ", "_").replaceAll("[(]", "").replaceAll("[)]", "").replaceAll("[+(]", "_").toUpperCase();
            return itemNames.getOrDefault(notedID, itemName);
        } else return itemNames.getOrDefault(id, null);
    }

    private static @NotNull List<RequirementDef> getRequirementDefs(Map<Integer, Map<Integer, Integer>> requirementMap) {
        List<RequirementDef> requirementDefs = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : requirementMap.entrySet()) {
            int itemId = entry.getKey();
            Map<Integer, Integer> skillMap = entry.getValue();
            for (Map.Entry<Integer, Integer> skillEntry : skillMap.entrySet()) {
                int skillId = skillEntry.getKey();
                int requirementLevel = skillEntry.getValue();
                RequirementDef def = new RequirementDef(getItemName(itemId), Skill.values()[skillId], requirementLevel);
                requirementDefs.add(def);
            }
        }
        return requirementDefs;
    }


    static class RequirementDef {
        final String name;
        final Skill skill;
        final int requirement;

        public RequirementDef(String name, Skill skill, int requirement) {
            this.name = name;
            this.skill = skill;
            this.requirement = requirement;
        }

        @Override
        public String toString() {
            return "RequirementDef{" +
                "name='" + name + '\'' +
                ", skill=" + skill +
                ", requirement=" + requirement +
                '}';
        }
    }
}
