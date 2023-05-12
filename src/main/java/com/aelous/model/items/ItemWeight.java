package com.aelous.model.items;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Situations on 12/31/2015.
 */
public class ItemWeight {
    private static final Map<Integer, Double> itemWeight = new HashMap<>();

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
        player.getPacketSender().sendString(15122, Utils.format((int) weight) + " kg");
        return weight;
    }

    private static double getWeight(int itemId) {
        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, itemId);
        return itemWeight.getOrDefault(def.id, def.getWeight());
    }
}
