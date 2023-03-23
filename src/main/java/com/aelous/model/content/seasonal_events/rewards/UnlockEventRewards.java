package com.aelous.model.content.seasonal_events.rewards;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 14, 2021
 */
public class UnlockEventRewards {

    private final Player player;

    public UnlockEventRewards(Player player) {
        this.player = player;
    }

    public Item generateReward() {
        var randomReward = World.getWorld().random(210);
        final List<AttributeKey> keyList = Arrays.asList(EVENT_REWARD_1_CLAIMED, EVENT_REWARD_2_CLAIMED, EVENT_REWARD_3_CLAIMED, EVENT_REWARD_4_CLAIMED, EVENT_REWARD_5_CLAIMED, EVENT_REWARD_6_CLAIMED, EVENT_REWARD_7_CLAIMED, EVENT_REWARD_8_CLAIMED, EVENT_REWARD_9_CLAIMED, EVENT_REWARD_10_CLAIMED, EVENT_REWARD_11_CLAIMED, EVENT_REWARD_12_CLAIMED, EVENT_REWARD_13_CLAIMED, EVENT_REWARD_14_CLAIMED, EVENT_REWARD_15_CLAIMED, EVENT_REWARD_16_CLAIMED, EVENT_REWARD_17_CLAIMED, EVENT_REWARD_18_CLAIMED, EVENT_REWARD_19_CLAIMED, EVENT_REWARD_20_CLAIMED, EVENT_REWARD_21_CLAIMED, EVENT_REWARD_22_CLAIMED, EVENT_REWARD_23_CLAIMED, EVENT_REWARD_24_CLAIMED, EVENT_REWARD_25_CLAIMED, EVENT_REWARD_26_CLAIMED, EVENT_REWARD_27_CLAIMED, EVENT_REWARD_28_CLAIMED, EVENT_REWARD_29_CLAIMED, EVENT_REWARD_30_CLAIMED, EVENT_REWARD_31_CLAIMED, EVENT_REWARD_32_CLAIMED, EVENT_REWARD_33_CLAIMED, EVENT_REWARD_34_CLAIMED, EVENT_REWARD_35_CLAIMED, EVENT_REWARD_36_CLAIMED, EVENT_REWARD_37_CLAIMED, EVENT_REWARD_38_CLAIMED, EVENT_REWARD_39_CLAIMED, EVENT_REWARD_40_CLAIMED, EVENT_REWARD_41_CLAIMED, EVENT_REWARD_42_CLAIMED, EVENT_REWARD_43_CLAIMED, EVENT_REWARD_44_CLAIMED);
        var unlockedAllRewards = keyList.stream().allMatch(key -> player.<Boolean>getAttribOr(key, false));
        if (unlockedAllRewards) {
            return null;//Extra safety
        }

        for (EventRewards reward : EventRewards.values()) {
            //System.out.println(reward.name()+" -> "+player.<Boolean>getAttribOr(reward.key,false));
            if (player.<Boolean>getAttribOr(reward.key, false)) {
                continue;
            }
            if (reward.chance <= randomReward) {
                player.putAttrib(reward.key, true);
                return reward.reward;
            }
        }
        final EventRewards eventRewards = Arrays.stream(EventRewards.values()).filter(r -> !player.<Boolean>getAttribOr(r.key, false)).findAny().get();
        player.putAttrib(eventRewards.key, true);
        return eventRewards.reward;
    }

    public void reset() {
        final List<AttributeKey> keyList = Arrays.asList(EVENT_REWARD_1_CLAIMED, EVENT_REWARD_2_CLAIMED, EVENT_REWARD_3_CLAIMED, EVENT_REWARD_4_CLAIMED, EVENT_REWARD_5_CLAIMED, EVENT_REWARD_6_CLAIMED, EVENT_REWARD_7_CLAIMED, EVENT_REWARD_8_CLAIMED, EVENT_REWARD_9_CLAIMED, EVENT_REWARD_10_CLAIMED, EVENT_REWARD_11_CLAIMED, EVENT_REWARD_12_CLAIMED, EVENT_REWARD_13_CLAIMED, EVENT_REWARD_14_CLAIMED, EVENT_REWARD_15_CLAIMED, EVENT_REWARD_16_CLAIMED, EVENT_REWARD_17_CLAIMED, EVENT_REWARD_18_CLAIMED, EVENT_REWARD_19_CLAIMED, EVENT_REWARD_20_CLAIMED, EVENT_REWARD_21_CLAIMED, EVENT_REWARD_22_CLAIMED, EVENT_REWARD_23_CLAIMED, EVENT_REWARD_24_CLAIMED, EVENT_REWARD_25_CLAIMED, EVENT_REWARD_26_CLAIMED, EVENT_REWARD_27_CLAIMED, EVENT_REWARD_28_CLAIMED, EVENT_REWARD_29_CLAIMED, EVENT_REWARD_30_CLAIMED, EVENT_REWARD_31_CLAIMED, EVENT_REWARD_32_CLAIMED, EVENT_REWARD_33_CLAIMED, EVENT_REWARD_34_CLAIMED, EVENT_REWARD_35_CLAIMED, EVENT_REWARD_36_CLAIMED, EVENT_REWARD_37_CLAIMED, EVENT_REWARD_38_CLAIMED, EVENT_REWARD_39_CLAIMED, EVENT_REWARD_40_CLAIMED, EVENT_REWARD_41_CLAIMED, EVENT_REWARD_42_CLAIMED, EVENT_REWARD_43_CLAIMED, EVENT_REWARD_44_CLAIMED);
        var unlockedAllRewards = keyList.stream().allMatch(key -> player.<Boolean>getAttribOr(key, false));

        //We've unlock all rewards we can go ahead and reset them as requested.
        if (unlockedAllRewards) {
            player.optionsTitled("Would you like to reset all the rewards?", "Yes", "No", () -> {

                for (AttributeKey key : keyList) {
                    player.putAttrib(key, false);
                }

                player.putAttrib(WINTER_EVENT_TOKENS_SPENT,0);
                player.inventory().addOrBank(COMPLETED_EVENT_REWARD);
                //Let the world know this play has finished the rewards and goes for them again
                World.getWorld().sendWorldMessage("<img=2010><shad=0>" + Color.MEDRED.wrap("[Winter event]:") + "</shad> " + Color.PURPLE.wrap(player.getUsername()) + " has just reset the " + Color.PURPLE.wrap("Winter event") + " rewards!");

                player.getInterfaceManager().close();
            });
        } else {
            player.message(Color.RED.wrap("You haven't unlocked all the possible rewards from this event yet."));
        }
    }

    private final List<Integer> items_to_shout = Arrays.asList(
        DONATOR_MYSTERY_BOX, ARMADYL_GODSWORD, LEGENDARY_MYSTERY_BOX, RAIDS_MYSTERY_BOX, MYSTERY_TICKET, CHRISTMAS_CRACKER);

    public void rollForReward(int tokens, int amount, Item reward) {
        player.inventory().remove(tokens, amount);
        player.inventory().addOrBank(reward);

        final int tokensSpent = player.<Integer>getAttribOr(WINTER_EVENT_TOKENS_SPENT,0) + amount;
        player.putAttrib(WINTER_EVENT_TOKENS_SPENT, tokensSpent);
        final var progress = (int) (tokensSpent * 100 / (double) 220_000);

        player.getPacketSender().sendProgressBar(73313, progress);
        player.getPacketSender().sendString(73314, "Tokens spent: " +tokensSpent);

        boolean shout = items_to_shout.stream().anyMatch(item -> reward.getId() == item);

        if (shout) {
            //The user box test doesn't yell.
            if(player.getUsername().equalsIgnoreCase("Box test")) {
                return;
            }
            World.getWorld().sendWorldMessage("<img=2010><shad=0>" + Color.MEDRED.wrap("[News]:") + "</shad> " + Color.PURPLE.wrap(player.getUsername()) + " received " + Color.HOTPINK.wrap("x" + reward.getAmount()) + " " + Color.HOTPINK.wrap(reward.unnote().name()) + " from the Event!");
        } else {
            int rewardAmount = reward.getAmount();
            String plural = rewardAmount > 1 ? "x" + rewardAmount : "x1";
            player.message(Color.BLUE.tag() + "Congratulations, you have received " + plural + " " + reward.unnote().name() + "!");
        }
    }

    private static final int INTERFACE_ID = 73300;
    private static final int COMPLETED_EVENT_REWARD_SLOT = 73315;
    private static final int TOKEN_REQUIREMENT_SLOT = 73316;
    public static final int UNLOCKED_ITEM_SLOT = 73317;
    private static final int UNLOCKABLE_ITEMS_CONTAINER = 73318;

    private static final Item COMPLETED_EVENT_REWARD = new Item(BLOOD_MONEY, 100_000);

    private static final ArrayList<Item> items = new ArrayList<>();

    static {
        //Fill the map with all potential rewards
        for (EventRewards reward : EventRewards.values()) {
            items.add(reward.reward);
        }
    }

    public void open() {
        setAllItemsTrans();

        //Refresh them
        refreshItems();

        player.getInterfaceManager().open(INTERFACE_ID);

        player.getPacketSender().sendItemOnInterface(UNLOCKABLE_ITEMS_CONTAINER, items);

        final int tokensSpent = player.<Integer>getAttribOr(WINTER_EVENT_TOKENS_SPENT,0);
        player.putAttrib(WINTER_EVENT_TOKENS_SPENT, tokensSpent);
        final var progress = (int) (tokensSpent * 100 / (double) 220_000);
        player.getPacketSender().sendProgressBar(73313, progress);
        player.getPacketSender().sendString(73314, "Tokens spent: " +tokensSpent);

        //Write the event reward item
        player.getPacketSender().sendItemOnInterfaceSlot(COMPLETED_EVENT_REWARD_SLOT, COMPLETED_EVENT_REWARD, 0);

        //Clear the rolled item slot
        player.getPacketSender().sendItemOnInterfaceSlot(UNLOCKED_ITEM_SLOT, null, 0);
    }

    private void setAllItemsTrans() {
        for (Item item : items) {
            item.setAmount(0);
        }
    }

    public void refreshItems() {
        for (EventRewards reward : EventRewards.values()) {
            var unlocked = player.<Boolean>getAttribOr(reward.key, false);
            if (unlocked) {
                reward.reward.setAmount(1);
                player.getPacketSender().sendItemOnInterface(UNLOCKABLE_ITEMS_CONTAINER, items);
            }
        }
    }
}
