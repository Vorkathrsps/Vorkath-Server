package com.aelous.model.content.items.combine;

import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

public class DragonHunterLance extends PacketInteraction {

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == HYDRAS_CLAW || usedWith.getId() == HYDRAS_CLAW) && (use.getId() == ZAMORAKIAN_HASTA || usedWith.getId() == ZAMORAKIAN_HASTA)) {
            if (player.inventory().containsAll(HYDRAS_CLAW, ZAMORAKIAN_HASTA)) {
                player.confirmDialogue(new String[]{"Are you sure you wish to combine the Hydra claw and the", "Zamorakian hasta to create the Dragon hunter lance", "This can not be reversed."}, "", "Proceed with the combination.", "Cancel.", () -> {
                    if(!player.inventory().containsAll(HYDRAS_CLAW, ZAMORAKIAN_HASTA)) {
                        return;
                    }
                    player.animate(4462);
                    player.graphic(759, GraphicHeight.LOW,0);
                    player.inventory().remove(HYDRAS_CLAW);
                    player.inventory().remove(ZAMORAKIAN_HASTA);
                    player.inventory().add(new Item(DRAGON_HUNTER_LANCE));
                    player.message("You successfully combine the Hydra claw and the Zamorakian hasta to create the");
                    player.message("Dragon hunter lance.");
                    player.itemDialogue("You successfully combine the Hydra claw and the<br>Zamorakian hasta to create the Dragon hunter lance.", DRAGON_HUNTER_LANCE);
                });
            }
            return true;
        }
        return false;
    }
}
