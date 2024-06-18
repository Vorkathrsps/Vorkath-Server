package com.cryptic.model.content.raids.chamber_of_xeric.reward;

import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.cryptic.utility.Varbit;

import java.security.SecureRandom;

import static com.cryptic.model.content.collection_logs.CollectionLog.RAIDS_KEY;
import static com.cryptic.model.content.collection_logs.LogType.OTHER;
import static com.cryptic.utility.ItemIdentifiers.*;

public class ChamberOfXericReward {

    public static void withdrawReward(Player player) {
        Item[] rewards = player.getRaidRewards().getItems();
        player.inventory().addOrBank(rewards);
        for (Item item : rewards) {
            if (item == null) continue;
            if (ChamberLootTable.uniqueTable.allItems().stream().anyMatch(i -> item.matchesId(item.getId()))) {
                Utils.sendDiscordInfoLog("Rare drop collected: " + player.getUsername() + " withdrew " + item.unnote().name() + " ", "raids");
                if (item.getValue() > 50_000) {
                    String worldMessage = "<img=2013>[<col=" + Color.RAID_PURPLE.getColorValue() + ">Chambers of Xerics</col>]</shad></col>: " + Color.BLUE.wrap(player.getUsername()) + " received " + Utils.getAOrAn(item.unnote().name()) + " <shad=0><col=AD800F>" + item.unnote().name() + "</shad>!";
                    World.getWorld().sendWorldMessage(worldMessage);
                }
            }
        }

        player.getRaidRewards().clear();
        player.varps().varbit(Varbit.RAIDS_REWARD, 0);
        SecureRandom secureRandom = new SecureRandom();

        if (secureRandom.nextInt() >= 650) {
            ChamberLootTable.unlockOlmlet(player);
        }
    }

    public static void displayRewards(Player player) {
        int totalRewards = player.getRaidRewards().getItems().length;

        player.getPacketSender().sendItemOnInterfaceSlot(12022, null, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(12023, null, 0);
        player.getPacketSender().sendItemOnInterfaceSlot(12024, null, 0);

        player.getInterfaceManager().open(12020);

        if (totalRewards >= 1) {
            Item reward1 = player.getRaidRewards().getItems()[0];
            player.getPacketSender().sendItemOnInterfaceSlot(12022, reward1, 0);
        }

        if (totalRewards >= 2) {
            Item reward2 = player.getRaidRewards().getItems()[1];
            player.getPacketSender().sendItemOnInterfaceSlot(12023, reward2, 0);
        }

        if (getChambersCompleted(player) < 1) {
            player.getPacketSender().sendItemOnInterfaceSlot(12024, new Item(DARK_JOURNAL), 0);
        }
    }

    public static int getChambersCompleted(Player player) {
        return player.getAttribOr(AttributeKey.CHAMBER_OF_SECRET_RUNS_COMPLETED, 0);
    }

    public static void giveRewards(Player player) {
        SecureRandom secureRandom = new SecureRandom();

        int personalPoints = player.getAttribOr(AttributeKey.PERSONAL_POINTS, 0);
        float chance = (float) (Math.ceil(personalPoints / 100D) / 100D);
        int points_cap = 570_000;

        Item rollUnique = ChamberLootTable.rollUnique();
        Item rollRegular = ChamberLootTable.rollRegular();

        if (personalPoints <= 10_000) {
            player.message("You need at least 10k points to get a drop from Raids.");
            return;
        }

        handleChambersOfXericAchievements(player);

        if (personalPoints > points_cap) {
            personalPoints = points_cap;
        }

        if (secureRandom.nextFloat() >= chance) {
            player.varps().varbit(Varbit.RAIDS_CHEST, 3);
            boolean added = player.getRaidRewards().add(rollUnique);
            OTHER.log(player, RAIDS_KEY, rollUnique);
            player.varps().varbit(Varbit.RAIDS_REWARD, 2);
            Utils.sendDiscordInfoLog("Rare drop: " + player.getUsername() + " Has just received " + rollUnique.unnote().name() + " from Chambers of Xeric! Party Points: " + Utils.formatNumber(personalPoints) + " [debug: added=" + added + "]", "raids");
        } else {
            player.varps().varbit(Varbit.RAIDS_CHEST, 1);
            player.varps().varbit(Varbit.RAIDS_REWARD, 1);
            player.getRaidRewards().add(rollRegular);
            Utils.sendDiscordInfoLog("Regular Drop: " + player.getUsername() + " Has just received " + rollRegular.unnote().name() + " from Chambers of Xeric! Personal Points: " + Utils.formatNumber(personalPoints), "raids");
        }
    }

    private static void handleChambersOfXericAchievements(Player player) {
        AchievementsManager.activate(player, Achievements.COX_I, 1);
        AchievementsManager.activate(player, Achievements.COX_II, 1);
        AchievementsManager.activate(player, Achievements.COX_III, 1);
    }
}
