package com.cryptic.model.content.items.interactions;

import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.CustomItemIdentifiers;

public class MysteryBox extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == CustomItemIdentifiers.BOX_OF_VALOR) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            }
        }
        return false;
    }
}
