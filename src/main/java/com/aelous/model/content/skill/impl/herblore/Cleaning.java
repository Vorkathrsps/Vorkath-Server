package com.aelous.model.content.skill.impl.herblore;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public class Cleaning {

    private enum Herb {
        GUAM(1 /* Level 3, but on rs you do druidic ritual */, "guam leaf", 199, 249, 2.5, "a"),
        ARDIGAL(1, "ardigal", 1527, 1528, 2.5, "an"),
        SITO_FOIL(1, "sito foil", 1529, 1530, 2.5, "a"),
        VOLENCIA_MOSS(1, "volencia moss", 1531, 1532, 2.5, "a"),
        ROGUESPURSE(3, "rogue's purse", 1533, 1534, 2.5, "a"),
        SNAKEWEED(3, "snake weed", 1525, 1526, 2.5, "a"),
        MARRENTILL(5, "marrentill", 201, 251, 5.0, "a"),
        TARROMIN(11, "tarromin", 203, 253, 5.0, "a"),
        HARRALANDER(20, "harralander", 205, 255, 6.3, "a"),
        RANNAR(25, "rannar weed", 207, 257, 7.5, "a"),
        TOADFLAX(30, "toadflax", 3049, 2998, 8.0, "a"),
        IRIT(40, "irit leaf", 209, 259, 8.8, "an"),
        AVANTOE(48, "avantoe", 211, 261, 10.0, "an"),
        KWUARM(54, "kwuarm", 213, 263, 11.3, "a"),
        SNAPDRAGON(59, "snapdragon", 3051, 3000, 11.8, "a"),
        CADANTINE(65, "cadantine", 215, 265, 12.5, "a"),
        LANTADYME(67, "lantadyme", 2485, 2481, 13.1, "a"),
        DRAWFWEED(70, "dwarf weed", 217, 267, 13.8, "a"),
        TORSTOL(75, "torstol", 219, 269, 15.0, "a");

        private int level, grimy, clean;
        private double exp;
        private String herbName, AorAn;

        Herb(int level, String herbName, int grimy, int clean, double exp, String AorAn) {
            this.level = level;
            this.herbName = herbName;
            this.grimy = grimy;
            this.clean = clean;
            this.exp = exp;
            this.AorAn = AorAn;
        }

         static Map<Integer, Herb> herb = new HashMap<>();

        static {
            for (Herb herbs : Herb.values()) {
                herb.put(herbs.grimy, herbs);
            }
        }
    }

    public static boolean onItemOption1(Player player, Item item) {
        Herb herb = Herb.herb.get(item.getId());
        if (herb != null) {
            if (player.inventory().contains(herb.grimy)) {
                clean(player, herb);
                return true;
            }
        }
        return false;
    }

    private static void clean(Player player, Herb herb) {
        //Does the player have the required level?
        if (player.getSkills().level(Skills.HERBLORE) < herb.level) {
            player.message("You need level "+herb.level+" Herblore to clean "+herb.AorAn+" "+herb.herbName+".");
            return;
        }
        //Replace the grimy with clean, send message, and add experience.
        int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, 0);
        player.inventory().remove(new Item(herb.grimy), slot, true);
        player.inventory().add(new Item(herb.clean), true);
        player.message("You clean the "+herb.herbName+".");
        player.getSkills().addXp(Skills.HERBLORE, herb.exp);
    }
}
