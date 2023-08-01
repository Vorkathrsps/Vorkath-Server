package com.cryptic.model.items;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Situations on 12/31/2015.
 */
public class ItemWeight {
    private static Map<Integer, Double> itemWeight = new HashMap<>();

    public static double calculateWeight(Player player) {
        double weight = 0;

        for (Item item : player.inventory()) {
            if (item != null) {
                weight += getWeight(item.getId());
            }
        }

        for (Item item : player.getEquipment()) {
            if (item != null) {
                weight += getWeight(item.getId());
            }
        }

        player.setWeight(weight);
        player.getPacketSender().sendWeight(weight);
        player.getPacketSender().sendString(184, Utils.format((int) weight) + " kg");
        return weight;
    }

    private static double getWeight(int itemId) {
        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, itemId);
        return itemWeight.getOrDefault(def.id, def.getWeight());
    }
}
