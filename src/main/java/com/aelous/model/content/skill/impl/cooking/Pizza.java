package com.aelous.model.content.skill.impl.cooking;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 15, 2020
 */
public class Pizza {

    public static boolean makePizza(Player player, Item used, Item with) {
        if(!player.inventory().contains(used) && player.inventory().contains(with)) {
            return false;
        }

        if ((used.getId() == PLAIN_PIZZA && with.getId() == COOKED_MEAT) || (used.getId() == COOKED_MEAT && with.getId() == PLAIN_PIZZA)) {
            if(player.getSkills().levels()[Skills.COOKING] < 45) {
                player.message("You need a cooking level of at least 45 to make this pizza.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.PLAIN_PIZZA));
            player.inventory().remove(new Item(ItemIdentifiers.COOKED_MEAT));
            player.inventory().add(new Item(ItemIdentifiers.MEAT_PIZZA));
            player.getSkills().addXp(Skills.COOKING, 143.0);
            return true;
        } else if ((used.getId() == PLAIN_PIZZA && with.getId() == COOKED_CHICKEN) || (used.getId() == COOKED_CHICKEN && with.getId() == PLAIN_PIZZA)) {
            if(player.getSkills().levels()[Skills.COOKING] < 45) {
                player.message("You need a cooking level of at least 45 to make this pizza.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.PLAIN_PIZZA));
            player.inventory().remove(new Item(COOKED_CHICKEN));
            player.inventory().add(new Item(ItemIdentifiers.MEAT_PIZZA));
            player.getSkills().addXp(Skills.COOKING, 143.0);
            return true;
        } else if ((used.getId() == PLAIN_PIZZA && with.getId() == ANCHOVIES) || (used.getId() == ANCHOVIES && with.getId() == PLAIN_PIZZA)) {
            if(player.getSkills().levels()[Skills.COOKING] < 55) {
                player.message("You need a cooking level of at least 55 to make this pizza.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.PLAIN_PIZZA));
            player.inventory().remove(new Item(ItemIdentifiers.ANCHOVIES));
            player.inventory().add(new Item(ANCHOVY_PIZZA));
            player.getSkills().addXp(Skills.COOKING, 182.0);
            return true;
        } else if ((used.getId() == PLAIN_PIZZA && with.getId() == PINEAPPLE_CHUNKS) || (used.getId() == PINEAPPLE_CHUNKS && with.getId() == PLAIN_PIZZA)) {
            if(player.getSkills().levels()[Skills.COOKING] < 65) {
                player.message("You need a cooking level of at least 65 to make this pizza.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.PLAIN_PIZZA));
            player.inventory().remove(new Item(ItemIdentifiers.PINEAPPLE_CHUNKS));
            player.inventory().add(new Item(PINEAPPLE_PIZZA));
            player.getSkills().addXp(Skills.COOKING, 195.0);
            return true;
        } else if ((used.getId() == PLAIN_PIZZA && with.getId() == PINEAPPLE_RING) || (used.getId() == PINEAPPLE_RING && with.getId() == PLAIN_PIZZA)) {
            if(player.getSkills().levels()[Skills.COOKING] < 65) {
                player.message("You need a cooking level of at least 65 to make this pizza.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.PLAIN_PIZZA));
            player.inventory().remove(new Item(ItemIdentifiers.PINEAPPLE_RING));
            player.inventory().add(new Item(PINEAPPLE_PIZZA));
            player.getSkills().addXp(Skills.COOKING, 195.0);
            return true;
        }
        return false;
    }

}
