package com.cryptic.model.content.items.interactions;

import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.ItemIdentifiers;

public class MysteryBox extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == CustomItemIdentifiers.BOX_OF_VALOR) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_1) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_2) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_3) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_4) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_5) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_6) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_7) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_8) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            } else if (item.getId() == ItemIdentifiers.BOUNTY_CRATE_TIER_9) {
                return CollectionItemHandler.rollBoxReward(player, item.getId());
            }
        }
        return false;
    }
}
