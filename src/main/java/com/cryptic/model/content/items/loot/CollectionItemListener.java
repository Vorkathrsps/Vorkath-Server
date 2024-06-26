package com.cryptic.model.content.items.loot;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface CollectionItemListener {
    @NotNull CollectionItem[] rewards();

    String name();

    int id();

    boolean isItem(int id);

    AttributeKey key();

    LogType logType();

    @NotNull
    private Item getSelectedItem(Item reward) {
        boolean rewardFound = false;
        int totalRarity = 0;
        for (var i : rewards()) totalRarity += i.rarity;
        int randomNumber = World.getWorld().random().nextInt(totalRarity);
        int cumulativeRarity = 0;
        List<CollectionItem> list = new ArrayList<>(Arrays.asList(rewards()));
        list.sort(Comparator.comparingInt(k -> k.rarity));
        list = list.reversed();
        while (!rewardFound) {
            for (var i : list) {
                cumulativeRarity += i.rarity;
                if (randomNumber < cumulativeRarity) {
                    if (i.amount == -1) reward = new Item(i.id);
                    else reward = new Item(i.id, World.getWorld().random(i.amount / 2, i.amount));
                    rewardFound = true;
                    break;
                }
            }
        }
        return reward;
    }

    default void openKey(Player player) {
        player.animate(536);
        player.getInventory().remove(this.id());
        handleAchievements(player);
        int increment = player.<Integer>getAttribOr(this.key(), 0) + 1;
        Item reward = null;
        reward = getSelectedItem(reward);
        boolean isRare = false;
        for (var i : rewards()) {
            if (i.isRare && i.id == reward.getId()) {
                isRare = true;
                break;
            }
        }
        if (!WildernessArea.inWilderness(player.tile())) player.getInventory().addOrBank(reward);
        else player.getInventory().addOrDrop(reward);
        this.logType().log(player, this.id(), reward);
        String name = reward.name();
        if (reward.noted()) name = reward.unnote().name();
        if (isRare) {
            World.getWorld().sendWorldMessage("<lsprite=2010><shad=0>[<col=" + Color.YELLOW.getColorValue() + ">" + this.name() + "</col>]</shad>:<col=AD800F> " + "<shad=0>" + Color.YELLOW.wrap(player.getUsername() + " received a ") + "</shad>" + "<shad=0>" + Color.BURNTORANGE.wrap(name + "!") + "</shad>");
            Utils.sendDiscordInfoLog("Player " + player.getUsername() + " received a " + name + " from a " + this.name() + ".", "box_and_tickets");
        }
        player.putAttrib(this.key(), increment);
    }

    private void handleAchievements(Player player) {
        if (ItemIdentifiers.CRYSTAL_KEY == this.id()) {
            AchievementsManager.activate(player, Achievements.WHATS_INSIDE_I, 1);
            AchievementsManager.activate(player, Achievements.WHATS_INSIDE_II, 1);
            AchievementsManager.activate(player, Achievements.WHATS_INSIDE_III, 1);
        } else if (ItemIdentifiers.LARRANS_KEY == this.id()) {
            AchievementsManager.activate(player, Achievements.LARRAN_FRENZY_I, 1);
            AchievementsManager.activate(player, Achievements.LARRAN_FRENZY_II, 1);
            AchievementsManager.activate(player, Achievements.LARRAN_FRENZY_III, 1);
        }
    }

    default void openClue(Player player) {
        Item reward = null;
        player.getInventory().remove(this.id(), 1);
        int increment = player.<Integer>getAttribOr(this.key(), 0) + 1;
        List<Item> rewards = new ArrayList<>();
        Map<Boolean, Integer> rareMap = new HashMap<>();
        for (int index = 0; index < World.getWorld().random(2, 4); index++) {
            reward = getSelectedItem(reward);
            rewards.add(reward);
            for (var i : rewards()) {
                if (i.isRare && i.id == reward.getId()) {
                    rareMap.put(true, i.id);
                }
            }
        }
        player.putAttrib(this.key(), increment);
        player.clueScrollReward().addAll(rewards);
        player.getPacketSender().sendItemOnInterface(6963, player.clueScrollReward().toArray());
        player.getInterfaceManager().open(6960);
        player.clearAttrib(AttributeKey.CLUE_SCROLL_REWARD);
        for (Item rolledReward : rewards) {
            if (!WildernessArea.inWilderness(player.tile())) player.getInventory().addOrBank(rolledReward);
            else player.getInventory().addOrDrop(rolledReward);
            if (this.logType() != null) this.logType().log(player, this.id(), rolledReward);
            for (var rare : rareMap.entrySet()) {
                if (rare.getValue() == rolledReward.getId()) {
                    World.getWorld().sendWorldMessage("<lsprite=2010><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">" + this.name() + "</col>]</shad>:<col=AD800F> " + player.getUsername() + " received a <shad=0>" + rolledReward.name() + "</shad>!");
                }
            }
        }
    }

    default void openBox(Player player) {
        Item reward;
        player.getInventory().remove(this.id(), 1);
        int increment = player.<Integer>getAttribOr(this.key(), 0) + 1;
        List<Item> rewards = new ArrayList<>();
        boolean isRare = false;
        int totalRarity = 0;
        for (var i : rewards()) totalRarity += i.rarity;
        int randomNumber = World.getWorld().random().nextInt(totalRarity);
        int cumulativeRarity = 0;
        for (int rollCount = 0; rollCount < 1; rollCount++) {
            boolean rewardFound = false;
            while (!rewardFound) {
                for (var i : rewards()) {
                    cumulativeRarity += i.rarity;
                    if (randomNumber < cumulativeRarity) {
                        if (i.amount == -1) reward = new Item(i.id);
                        else reward = new Item(i.id, World.getWorld().random(i.amount / 2, i.amount));
                        rewards.add(reward);
                        if (i.isRare) isRare = true;
                        rewardFound = true;
                        break;
                    }
                }
            }
        }
        if (this.key() != null) {
            player.putAttrib(this.key(), increment);
        }
        for (Item rolledReward : rewards) {
            if (!WildernessArea.inWilderness(player.tile())) player.getInventory().addOrBank(rolledReward);
            else player.getInventory().addOrDrop(rolledReward);
            if (this.logType() != null) this.logType().log(player, this.id(), rolledReward);
            if (isRare) {
                World.getWorld().sendWorldMessage("<lsprite=2010><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">" + this.name() + "</col>]</shad>:<col=AD800F> " + player.getUsername() + " received a <shad=0>" + rolledReward.name() + "</shad>!");
                Utils.sendDiscordInfoLog("Player " + player.getUsername() + " received a " + rolledReward.name() + " from a " + this.name() + ".", "box_and_tickets");
            }
        }
    }
}
