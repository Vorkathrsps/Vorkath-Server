package com.cryptic.model.content.items.boxes;

import com.cryptic.model.World;
import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface MysteryBoxListener {
    @NotNull MysteryBoxItem[] rewards();
    String name();
    int id();
    boolean isItem(int id);
    AttributeKey key();
    LogType logType();
    default void openKey(Player player, GameObject object, int oldId, int newId) {
        player.lock();
        player.animate(536);
        player.getInventory().remove(this.id());
        int increment = player.<Integer>getAttribOr(this.key(), 0) + 1;
        Item reward = null;
        reward = getSelectedItem(reward);
        Chain.bound(null).runFn(1, () -> {
            if (object != null) {
                GameObject oldObject = new GameObject(oldId, object.tile(), object.getType(), object.getRotation());
                GameObject newObject = new GameObject(newId, object.tile(), object.getType(), object.getRotation());
                ObjectManager.replace(oldObject, newObject, 2);
            }
            player.unlock();
        });
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
            World.getWorld().sendWorldMessage("<img=505><shad=0>[<col=" + Color.YELLOW.getColorValue() + ">" + this.name() + "</col>]</shad>:<col=AD800F> " + "<shad=0>" + Color.ADAMANTITE.wrap(player.getUsername() + " received a ") + "</shad>" + "<shad=0>" + Color.RAID_PURPLE.wrap(name + "!") + "</shad>");
            Utils.sendDiscordInfoLog("Player " + player.getUsername() + " received a " + name + " from a " + this.name() + ".", "box_and_tickets");
        }
        player.putAttrib(this.key(), increment);
    }

    @NotNull
    private Item getSelectedItem(Item reward) {
        boolean rewardFound = false;
        int totalRarity = 0;
        for (var i : rewards()) totalRarity += i.rarity;
        int randomNumber = World.getWorld().random().nextInt(totalRarity);
        int cumulativeRarity = 0;
        while (!rewardFound) {
            for (var i : rewards()) {
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

    default void openBox(Player player) {
        Item reward;
        player.getInventory().remove(this.id());
        int increment = player.<Integer>getAttribOr(this.key(), 0) + 1;
        List<Item> rewards = new ArrayList<>();
        boolean isRare = false;
        for (int rollCount = 0; rollCount < 1; rollCount++) {
            boolean rewardFound = false;
            while (!rewardFound) {
                for (var i : rewards()) {
                    if (World.getWorld().rollDie(i.rarity, 1)) {
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
        player.putAttrib(this.key(), increment);
        for (Item rolledReward : rewards) {
            if (!WildernessArea.inWilderness(player.tile())) player.getInventory().addOrBank(rolledReward);
            else player.getInventory().addOrDrop(rolledReward);
            this.logType().log(player, this.id(), rolledReward);
            if (isRare) {
                World.getWorld().sendWorldMessage("<img=505><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">" + this.name() + "</col>]</shad>:<col=AD800F> " + player.getUsername() + " received a <shad=0>" + rolledReward.name() + "</shad>!");
                Utils.sendDiscordInfoLog("Player " + player.getUsername() + " received a " + rolledReward.name() + " from a " + this.name() + ".", "box_and_tickets");
            }
        }
    }
}
