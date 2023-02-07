package com.aelous.model.content.items.combine;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 *
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date maart 23, 2021 13:35
 */
public class FerociousGloves extends PacketInteraction {

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == HAMMER || usedWith.getId() == HAMMER) && (use.getId() == HYDRA_LEATHER || usedWith.getId() == HYDRA_LEATHER)) {
            if(!player.inventory().contains(HYDRA_LEATHER))
                return true;
            player.inventory().remove(HYDRA_LEATHER);
            player.inventory().add(new Item(FEROCIOUS_GLOVES));
            player.message("By feeding the tough to work leather through the machine, you manage to form a pair");
            player.message("of gloves.");
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if(option == 1) {
            if(item.getId() == HYDRA_LEATHER) {
                player.message("This leather looks pretty tough to work with... Maybe the dragonkin had a way.");
                return true;
            }
        }
        return false;
    }
}
