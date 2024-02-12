package com.cryptic.model.content.sigils.interactions;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;

public class LastRecallInteraction extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == 26143) {
                if (player.getLastSavedTile() != null) {
                    player.teleport(player.getLastSavedTile());
                    return true;
                } else {
                    player.message(Color.BLUE.wrap("You do not currently have a saved location."));
                    return true;
                }
            }
        }
        return false;
    }
}
