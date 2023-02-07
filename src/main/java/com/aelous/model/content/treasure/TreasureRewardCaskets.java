package com.aelous.model.content.treasure;

import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.text.NumberFormat;

/**
 * Created by Situations on 2016-11-05.
 */
public class TreasureRewardCaskets {

    public static final int MASTER_CASKET = 19836;

    public static boolean openCasket(Player player, Item casket) {
        if (casket.getId() == MASTER_CASKET) {
            reward(player, new Item(MASTER_CASKET));
            return true;
        }
        return false;
    }

    private static void reward(Player player, Item casket) {
        if (player.inventory().remove(casket, true)) {
            Rewards.generateReward(player);

            //Add the reward to the players inventory, or bank if no space
            player.clueScrollReward().forEach(item -> {
                if (item != null) {
                    player.inventory().addOrBank(new Item(item.getId(), item.getAmount()));
                }
            });

            player.message("<col=3300ff>Your treasure is worth around " + NumberFormat.getInstance().format(value(player)) + " BM!</col>");

            //Display the interface
            //System.out.println(Arrays.toString(player.clueScrollReward().toArray()));
            player.getPacketSender().sendItemOnInterface(6963, player.clueScrollReward().toArray());
            player.getInterfaceManager().open(6960);

            //Clear the clue scroll reward and unlock the player
            player.clearAttrib(AttributeKey.CLUE_SCROLL_REWARD);
        }
    }

    // Calculate what the clue scroll's reward value is
    private static int value(Player player) {
        int rewardTotal = 0;

        for (Item reward : player.clueScrollReward()) {
            if (reward != null) {
                rewardTotal += reward.getValue() * reward.getAmount();
            }
        }

        return rewardTotal;
    }

}
