package com.aelous.model.content.skill.impl.cooking;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public class Potato {

    public static boolean makePotato(Player player, Item used, Item with) {
        if(!player.inventory().contains(used) && player.inventory().contains(with)) {
            return false;
        }

        if ((used.getId() == ItemIdentifiers.BAKED_POTATO && with.getId() == ItemIdentifiers.PAT_OF_BUTTER) || (used.getId() == ItemIdentifiers.PAT_OF_BUTTER && with.getId() == ItemIdentifiers.BAKED_POTATO)) {
            if(player.getSkills().levels()[Skills.COOKING] < 39) {
                player.message("You need a cooking level of at least 39 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.BAKED_POTATO));
            player.inventory().remove(new Item(ItemIdentifiers.PAT_OF_BUTTER));
            player.inventory().add(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.getSkills().addXp(Skills.COOKING, 40.0);
            return true;
        } else if ((used.getId() == ItemIdentifiers.POTATO_WITH_BUTTER && with.getId() == ItemIdentifiers.CHILLI_CON_CARNE) || (used.getId() == ItemIdentifiers.CHILLI_CON_CARNE && with.getId() == ItemIdentifiers.POTATO_WITH_BUTTER)) {
            if(player.getSkills().levels()[Skills.COOKING] < 39) {
                player.message("You need a cooking level of at least 39 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.inventory().remove(new Item(ItemIdentifiers.CHILLI_CON_CARNE));
            player.inventory().add(new Item(ItemIdentifiers.CHILLI_POTATO));
            player.getSkills().addXp(Skills.COOKING, 15.0);
            return true;
        } else if ((used.getId() == ItemIdentifiers.POTATO_WITH_BUTTER && with.getId() == ItemIdentifiers.CHEESE) || (used.getId() == ItemIdentifiers.CHEESE && with.getId() == ItemIdentifiers.POTATO_WITH_BUTTER)) {
            if(player.getSkills().levels()[Skills.COOKING] < 47) {
                player.message("You need a cooking level of at least 47 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.inventory().remove(new Item(ItemIdentifiers.CHEESE));
            player.inventory().add(new Item(ItemIdentifiers.POTATO_WITH_CHEESE));
            player.getSkills().addXp(Skills.COOKING, 40.0);
            return true;
        } else if ((used.getId() == ItemIdentifiers.POTATO_WITH_BUTTER && with.getId() == ItemIdentifiers.EGG_AND_TOMATO) || (used.getId() == ItemIdentifiers.EGG_AND_TOMATO && with.getId() == ItemIdentifiers.POTATO_WITH_BUTTER)) {
            if(player.getSkills().levels()[Skills.COOKING] < 51) {
                player.message("You need a cooking level of at least 51 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.inventory().remove(new Item(ItemIdentifiers.EGG_AND_TOMATO));
            player.inventory().add(new Item(ItemIdentifiers.EGG_POTATO));
            player.getSkills().addXp(Skills.COOKING, 45.0);
            return true;
        } else if ((used.getId() == ItemIdentifiers.POTATO_WITH_BUTTER && with.getId() == ItemIdentifiers.MUSHROOM__ONION) || (used.getId() == ItemIdentifiers.MUSHROOM__ONION && with.getId() == ItemIdentifiers.POTATO_WITH_BUTTER)) {
            if(player.getSkills().levels()[Skills.COOKING] < 64) {
                player.message("You need a cooking level of at least 64 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.inventory().remove(new Item(ItemIdentifiers.MUSHROOM__ONION));
            player.inventory().add(new Item(ItemIdentifiers.MUSHROOM_POTATO));
            player.getSkills().addXp(Skills.COOKING, 55.0);
            return true;
        } else if ((used.getId() == ItemIdentifiers.POTATO_WITH_BUTTER && with.getId() == ItemIdentifiers.TUNA_AND_CORN) || (used.getId() == ItemIdentifiers.TUNA_AND_CORN && with.getId() == ItemIdentifiers.POTATO_WITH_BUTTER)) {
            if(player.getSkills().levels()[Skills.COOKING] < 68) {
                player.message("You need a cooking level of at least 68 to make this potato.");
                return true;
            }

            player.inventory().remove(new Item(ItemIdentifiers.POTATO_WITH_BUTTER));
            player.inventory().remove(new Item(ItemIdentifiers.TUNA_AND_CORN));
            player.inventory().add(new Item(ItemIdentifiers.TUNA_POTATO));
            player.getSkills().addXp(Skills.COOKING, 10.0);
            return true;
        }
        return false;
    }
}
