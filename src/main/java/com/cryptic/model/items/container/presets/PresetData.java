package com.cryptic.model.items.container.presets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.items.Item;
import com.cryptic.utility.JGson;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Data
public class PresetData { //create data can be shared here
    private static final Logger logger = LogManager.getLogger(PresetData.class);
    private static final Gson gson = JGson.buildTypeAdapter();
    public int id;
    public String name;
    public Item[] inventory;
    public Item[] equipment;
    public MagicSpellbook spellbook;
    public int button;
    public AttributeKey attribute;
    public int cost;

    public PresetData build() {
        return this;
    }

    public PresetData id(int id) {
        this.id = id;
        return this;
    }

    public PresetData name(String value) {
        this.name = value;
        return this;
    }

    public PresetData inventory(Item[] value) {
        this.inventory = value;
        return this;
    }

    public PresetData equipment(Item[] value) {
        this.equipment = value;
        return this;
    }

    public PresetData spellBook(MagicSpellbook value) {
        this.spellbook = value;
        return this;
    }

    public PresetData attribute(AttributeKey attribute) {
        this.attribute = attribute;
        return this;
    }

    public PresetData button(int button) {
        this.button = button;
        return this;
    }

    public static List<PresetData> loadDefaultPresets(File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            TypeToken<List<PresetData>> typeToken = new TypeToken<>() {};
            return gson.fromJson(fileReader, typeToken.getType());
        }
    }

    @Override
    public String toString() {
        return "PresetData{" +
            ", id=" + id +
            ", inventory=" + Arrays.toString(inventory) +
            ", equipment=" + Arrays.toString(equipment) +
            ", spellbook=" + spellbook +
            ", button=" + button +
            ", attribute=" + attribute +
            '}';
    }

}
