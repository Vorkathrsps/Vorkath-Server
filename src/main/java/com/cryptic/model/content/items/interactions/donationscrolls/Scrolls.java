package com.cryptic.model.content.items.interactions.donationscrolls;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.CustomItemIdentifiers.*;

public class Scrolls extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        double rank = player.<Double>getAttribOr(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
        if (option == 1) {
            if (item.getId() == TWENTY_FIVE_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                rank += 25;
                adjustRank(player, TWENTY_FIVE_DOLLAR_SCROLL, rank, 25);
                return true;
            }
            if (item.getId() == FIFTY_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                rank += 50;
                adjustRank(player, FIFTY_DOLLAR_SCROLL, rank, 50);
                return true;
            }
            if (item.getId() == ONE_HUNDRED_DOLLAR_SCROLL) {
                if (player.isPerformingAction()) return true;
                rank += 100;
                adjustRank(player, ONE_HUNDRED_DOLLAR_SCROLL, rank, 100);
                return true;
            }
        }
        return false;
    }

    private void adjustRank(Player player, int scroll, double rank, int amount) {
        player.setPerformingAction(true);
        player.getInventory().remove(scroll);
        player.getInventory().add(DONATOR_TICKET, amount);
        player.putAttrib(AttributeKey.TOTAL_PAYMENT_AMOUNT, rank);
        player.getMemberRights().update(player, false);
        Chain.noCtx().delay(1, player::clearPerformingAction);
    }
}
