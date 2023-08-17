package com.cryptic.model.content.presets.newpreset;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PresetData {
    @SerializedName("id")
    private int id;

    @SerializedName("inventory")
    private List<String> inventory;

    @SerializedName("equipment")
    private List<String> equipment;

    @SerializedName("skills")
    private List<String> skills;

    @SerializedName("spellbook")
    private String spellbook;

    @SerializedName("button")
    private int button;

    @SerializedName("attribute")
    private int attribute;

    public PresetData(int id, List<String> inventory, List<String> equipment, List<String> skills, String spellbook, int button, int attribute) {
        this.id = id;
        this.inventory = inventory;
        this.equipment = equipment;
        this.skills = skills;
        this.spellbook = spellbook;
        this.button = button;
        this.attribute = attribute;
    }
}
