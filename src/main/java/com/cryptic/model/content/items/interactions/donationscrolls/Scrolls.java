package com.cryptic.model.content.items.interactions.donationscrolls;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.CustomItemIdentifiers.*;

public class Scrolls extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == TWENTY_FIVE_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().add(DONATOR_TICKET, 25);
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
            if (item.getId() == FIFTY_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().add(DONATOR_TICKET, 50);
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
            if (item.getId() == ONE_HUNDRED_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().add(DONATOR_TICKET, 100);
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
        }
        return false;
    }
}
