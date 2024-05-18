package com.cryptic.model.content.items.interactions;

import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class MysteryBox extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (item.getId() == 6199) {
            return CollectionItemHandler.rollBoxReward(player, item.getId());
        }
        return false;
    }
}
