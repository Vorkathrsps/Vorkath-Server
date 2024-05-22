package com.cryptic.model.content.items.interactions.cluescrolls;

import com.cryptic.model.content.items.loot.CollectionItemHandler;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class ClueScroll extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == ItemIdentifiers.CLUE_GEODE_BEGINNER) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.CLUE_SCROLL_BEGINNER));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_GEODE_EASY) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.CLUE_SCROLL_EASY));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_GEODE_MEDIUM) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.CLUE_SCROLL_MEDIUM));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_GEODE_HARD) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.CLUE_SCROLL_HARD));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_GEODE_ELITE) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.CLUE_SCROLL_ELITE));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }


            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_BEGINNER) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_BEGINNER));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_EASY) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_EASY));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }
            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_MEDIUM) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_MEDIUM));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_HARD) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_HARD));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_ELITE) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_ELITE));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.CLUE_SCROLL_MASTER) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                player.getInventory().remove(item);
                player.getInventory().add(new Item(ItemIdentifiers.REWARD_CASKET_MASTER));
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_BEGINNER) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_EASY) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_MEDIUM) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_HARD) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_ELITE) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

            if (item.getId() == ItemIdentifiers.REWARD_CASKET_MASTER) {
                if (player.isPerformingAction()) return true;
                player.setPerformingAction(true);
                CollectionItemHandler.rollClueScrollReward(player, item.getId());
                Chain.noCtx().delay(1, player::clearPerformingAction);
                return true;
            }

        }
        return false;
    }

}
