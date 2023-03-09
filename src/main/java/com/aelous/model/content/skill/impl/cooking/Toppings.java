package com.aelous.model.content.skill.impl.cooking;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public class Toppings {

    public static boolean makeTopping(Player player, Item used, Item with) {
        if (!player.inventory().contains(used) && player.inventory().contains(with)) {
            return false;
        }

        //Chopped garlic
        if ((used.getId() == ItemIdentifiers.GARLIC && with.getId() == ItemIdentifiers.BOWL) || (used.getId() == ItemIdentifiers.BOWL && with.getId() == ItemIdentifiers.GARLIC)) {
            if(player.inventory().contains(ItemIdentifiers.KNIFE)) {
                player.inventory().remove(new Item(ItemIdentifiers.GARLIC));
                player.inventory().remove(new Item(ItemIdentifiers.BOWL));
                player.inventory().add(new Item(ItemIdentifiers.CHOPPED_GARLIC));
            } else {
                player.message("I should use a knife to chop the garlic.");
            }
            return true;
        }

        //Spicy sauce
        if ((used.getId() == ItemIdentifiers.CHOPPED_GARLIC && with.getId() == ItemIdentifiers.GNOME_SPICE) || (used.getId() == ItemIdentifiers.GNOME_SPICE && with.getId() == ItemIdentifiers.CHOPPED_GARLIC)) {
            if(player.getSkills().levels()[Skills.COOKING] < 9) {
                player.message("You need a cooking level of at least 9.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.CHOPPED_GARLIC));
            player.inventory().remove(new Item(ItemIdentifiers.GNOME_SPICE));
            player.inventory().add(new Item(ItemIdentifiers.SPICY_SAUCE));
            player.getSkills().addXp(Skills.COOKING, 25.0);
            return true;
        }

        //Chili con carne
        if ((used.getId() == ItemIdentifiers.SPICY_SAUCE && with.getId() == ItemIdentifiers.COOKED_MEAT) || (used.getId() == ItemIdentifiers.COOKED_MEAT && with.getId() == ItemIdentifiers.SPICY_SAUCE)) {
            if(player.getSkills().levels()[Skills.COOKING] <  9) {
                player.message("You need a cooking level of at least 9.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.SPICY_SAUCE));
            player.inventory().remove(new Item(ItemIdentifiers.COOKED_MEAT));
            player.inventory().add(new Item(ItemIdentifiers.CHILLI_CON_CARNE));
            player.getSkills().addXp(Skills.COOKING, 25.0);
            return true;
        }

        //Egg and tomato
        if ((used.getId() == ItemIdentifiers.SCRAMBLED_EGG && with.getId() == ItemIdentifiers.TOMATO) || (used.getId() == ItemIdentifiers.TOMATO && with.getId() == ItemIdentifiers.SCRAMBLED_EGG)) {
            if(player.getSkills().levels()[Skills.COOKING] <  23) {
                player.message("You need a cooking level of at least 23.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.SPICY_SAUCE));
            player.inventory().remove(new Item(ItemIdentifiers.COOKED_MEAT));
            player.inventory().add(new Item(ItemIdentifiers.CHILLI_CON_CARNE));
            return true;
        }

        //TODO we need to add alot more toppings but don't really wanne do that now.
        return false;
    }
}
