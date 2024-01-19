package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.npc.droptables.util.DropsConverter;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemRepository {
    public static HashMap<String, Integer> itemIds = new HashMap<>();
    public static HashMap<Integer, String> itemNames = new HashMap<>();

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

    public static int getItemId(String name) {
        if (name.equals("null")) return -1;
        if (name.contains("NOTED_")) {
            Integer cached = itemIds.get(name);
            if (cached != null) return cached;
            int unnotedId = getItemId(name.substring("NOTED_".length()));
            ItemDefinition def = getItemDefinition(unnotedId);
            if (def != null) {
                itemIds.put(name, def.notelink);
                return def.notelink;
            }
        }
        return itemIds.getOrDefault(name, -1);
    }

    private static ItemDefinition getItemDefinition(int unnotedId) {
        return ItemDefinition.cached.get(unnotedId);
    }

    public static void load() {
        List<Field> item = Arrays.stream(ItemIdentifiers.class.getFields()).filter(field -> Modifier.isPublic(field.getModifiers())).toList();
        item.forEach(it -> {
            try {
                itemIds.put(it.getName(), it.getInt(ItemIdentifiers.class));
                itemNames.put(it.getInt(ItemIdentifiers.class), it.getName());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Loaded " + itemIds.size() + " item names");
    }
}

