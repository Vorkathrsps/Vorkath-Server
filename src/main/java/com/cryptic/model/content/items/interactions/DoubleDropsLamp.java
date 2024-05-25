package com.cryptic.model.content.items.interactions;

import com.cryptic.model.content.events.Events;
import com.cryptic.model.content.events.PlayerEvent;
import com.cryptic.model.content.events.TickToSeconds;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class DoubleDropsLamp extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == CustomItemIdentifiers.REVENANT_DROP_RATE_BOOST) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(CustomItemIdentifiers.REVENANT_DROP_RATE_BOOST);
                new PlayerEvent(player, Events.REVENANT_DROP_BOOST, TickToSeconds.get(TickToSeconds.THIRTY_MINUTES)).start();
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
            if (item.getId() == CustomItemIdentifiers.DOUBLE_DROPS_LAMP) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(CustomItemIdentifiers.DOUBLE_DROPS_LAMP);
                new PlayerEvent(player, Events.DOUBLE_DROPS, TickToSeconds.get(TickToSeconds.THIRTY_MINUTES)).start();
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
            if (item.getId() == CustomItemIdentifiers.DOUBLE_XP_LAMP) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(CustomItemIdentifiers.DOUBLE_XP_LAMP);
                new PlayerEvent(player, Events.DOUBLE_XP, TickToSeconds.get(TickToSeconds.THIRTY_MINUTES)).start();
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
        }
        return false;
    }
}
