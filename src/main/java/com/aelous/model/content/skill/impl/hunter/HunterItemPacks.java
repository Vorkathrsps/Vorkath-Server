package com.aelous.model.content.skill.impl.hunter;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public class HunterItemPacks {

    public static boolean onItemOption1(Player player, Item item) {
        if(item.getId() == MAGIC_IMP_BOX_PACK) {
            if (player.inventory().contains(MAGIC_IMP_BOX_PACK)) {
                player.inventory().remove(new Item(MAGIC_IMP_BOX_PACK), true);
                player.inventory().add(new Item(MAGIC_BOX, 100), true);
                player.message("You open the pack to find 100 magic boxes.");
            }
            return true;
        }
        if(item.getId() == BOX_TRAP_PACK) {
            if (player.inventory().contains(BOX_TRAP_PACK)) {
                player.inventory().remove(new Item(BOX_TRAP_PACK), true);
                player.inventory().add(new Item(BOX_TRAP + 1, 100), true);
                player.message("You open the pack to find 100 box traps.");
            }
            return true;
        }
        if(item.getId() == BIRD_SNARE_PACK) {
            if (player.inventory().contains(BIRD_SNARE_PACK)) {
                player.inventory().remove(new Item(BIRD_SNARE_PACK), true);
                player.inventory().add(new Item(BIRD_SNARE + 1, 100), true);
                player.message("You open the pack to find 100 bird snares.");
            }
            return true;
        }
        return false;
    }
}
