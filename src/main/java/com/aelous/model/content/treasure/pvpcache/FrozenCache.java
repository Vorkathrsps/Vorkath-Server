package com.aelous.model.content.treasure.pvpcache;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FrozenCache {

    public static int FROZEN_CACHE = 27622;

    public static boolean openCasket(Player player, Item casket) {
        if (casket.getId() == FROZEN_CACHE) {
            reward(player, new Item(FROZEN_CACHE));
            return true;
        }
        return false;
    }

    private static void reward(Player player, Item casket) {
        if (player.inventory().remove(casket, true)) {
            roll(player);

            player.clueScrollReward().forEach(item -> {
                if (item != null) {
                    player.inventory().addOrBank(new Item(item.getId(), item.getAmount()));
                }
            });

            player.getPacketSender().sendItemOnInterface(6963, player.clueScrollReward().toArray());
            player.getInterfaceManager().open(6960);

            player.clearAttrib(AttributeKey.CLUE_SCROLL_REWARD);
        }
    }

    public static void roll(Player player) {
        int rewards = World.getWorld().random(1, 2);
        SecureRandom randomChance = new SecureRandom();

        for (int index = 0; index < rewards; index++) {
            double roll = Utils.RANDOM_GEN.nextDouble() * 100.0;
            List<StandardRewards> possibles = Arrays.stream(StandardRewards.values()).filter(r -> roll <= r.probability).collect(Collectors.toList());

            StandardRewards reward = Utils.randomElement(possibles);
            if (reward != null)
                player.clueScrollReward().add(new Item(reward.reward.getId(), reward.reward.getAmount()), true);
        }

        if (randomChance.nextDouble() < 0.2) {
            double roll = Utils.RANDOM_GEN.nextDouble();
            List<RareRewards> possibles = Arrays.stream(RareRewards.values()).filter(r -> roll <= r.probability).collect(Collectors.toList());
            RareRewards reward = Utils.randomElement(possibles);
            if (reward != null) {
                player.clueScrollReward().add(new Item(reward.reward.getId(), reward.reward.getAmount()), true);
                if (!player.getUsername().equalsIgnoreCase("Box test"))
                    World.getWorld().sendWorldMessage("<img=2010> " + player.getUsername() + " has just received <col=" + Color.BLACK.getColorValue() + ">" + Utils.getAOrAn(reward.name()) + " " + reward.name() + " from a frozen pvp cache!");
            }
        }
    }
}
