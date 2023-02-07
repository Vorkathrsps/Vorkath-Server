package com.aelous.model.items.loot;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;
import com.google.gson.annotations.Expose;

/**
 * @author PVE
 * @Since augustus 29, 2020
 */
public class LootItem {

    @Expose public final int id;

    @Expose public final int min, max;

    @Expose public final int weight;

    public LootItem(int id, int amount, int weight) {
        this.id = id;
        this.min = amount;
        this.max = amount;
        this.weight = weight;
    }

    public LootItem(int id, int minAmount, int maxAmount, int weight) {
        this.id = id;
        this.min = minAmount;
        this.max = maxAmount;
        this.weight = weight;
    }

    public Item toItem() {
        Item item = new Item(id, min == max ? min : Utils.random(min, max));
        return item;
    }

    public String getName() {
        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, id);
        if(def.isNote())
            return World.getWorld().definitions().get(ItemDefinition.class, def.notelink).name + " (noted)";
        return def.name;
    }

}
