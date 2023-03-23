package com.aelous.model.content.raids.chamber_of_xeric.reward;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.Varbit;

import java.security.SecureRandom;

import static com.aelous.model.content.collection_logs.CollectionLog.RAIDS_KEY;
import static com.aelous.model.content.collection_logs.LogType.OTHER;
import static com.aelous.utility.ItemIdentifiers.*;

public class ChamberOfXericReward {

    public static void withdrawReward(Player player) {
        player.inventory().addOrBank(player.getRaidRewards().getItems());
        for (Item item : player.getRaidRewards().getItems()) {
            if (item == null)
                continue;
            if (ChamberLootTable.uniqueTable.allItems().stream().anyMatch(i -> item.matchesId(item.getId()))) {
                Utils.sendDiscordInfoLog("Rare drop collected: " + player.getUsername() + " withdrew " + item.unnote().name() + " ", "raids");
                if (item.getValue() > 50_000) {
                    String worldMessage = "<img=2013>[<col=" + Color.RAID_PURPLE.getColorValue() + ">Chambers of Xerics</col>]</shad></col>: " + Color.BLUE.wrap(player.getUsername()) + " received " + Utils.getAOrAn(item.unnote().name()) + " <shad=0><col=AD800F>" + item.unnote().name() + "</shad>!";
                    World.getWorld().sendWorldMessage(worldMessage);
                }
            }
        }

        player.getRaidRewards().clear();

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

        if (personalPoints > points_cap) {
            personalPoints = points_cap;
        }

        if (secureRandom.nextFloat() >= chance) {
            player.varps().varbit(Varbit.RAIDS_CHEST, 3);
            boolean added = player.getRaidRewards().add(rollUnique);
            OTHER.log(player, RAIDS_KEY, rollUnique);
            Utils.sendDiscordInfoLog("Rare drop: " + player.getUsername() + " Has just received " + rollUnique.unnote().name() + " from Chambers of Xeric! Party Points: " + Utils.formatNumber(personalPoints) + " [debug: added=" + added + "]", "raids");
        } else {
            player.varps().varbit(Varbit.RAIDS_CHEST, 1);
            player.getRaidRewards().add(rollRegular);
            Utils.sendDiscordInfoLog("Regular Drop: " + player.getUsername() + " Has just received " + rollRegular.unnote().name() + " from Chambers of Xeric! Personal Points: " + Utils.formatNumber(personalPoints), "raids");
        }
    }
}
