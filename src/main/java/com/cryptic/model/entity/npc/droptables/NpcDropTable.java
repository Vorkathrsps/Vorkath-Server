package com.cryptic.model.entity.npc.droptables;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NpcDropTable {
    @JsonProperty("npcId")
    private int[] npcId;
    @JsonProperty("petItem")
    private String petItem;
    @JsonProperty("petRarity")
    private int petRarity;
    @JsonProperty("alwaysDrops")
    private List<ItemDrop> alwaysDrops;
    @JsonProperty("drops")
    private List<ItemDrop> drops;
    @JsonProperty("rolls")
    private int rolls;

    public void postLoad() {
        drops.sort(Comparator.comparingInt(ItemDrop::getChance));
    }

    public List<Item> getDrops(Player player) {
        List<Item> list = new ArrayList<>();
        if (alwaysDrops != null && !alwaysDrops.isEmpty()) {
            for (var drops : alwaysDrops) {
                list.add(new Item(ItemRepository.getItemId(drops.getItem()), World.getWorld().random(Math.max(drops.getMinimumAmount(), 1), Math.max(drops.getMaximumAmount(), 1))));
            }
        }
        double rateBonus = player.getDropRateBonus() - 1D;
        List<ItemDrop> temp = new ArrayList<>(drops);
        for (int i = 0; i < this.rolls; i++) {
            Collections.shuffle(temp);
            if (!temp.isEmpty()) {
                for (var drop : temp) {
                    int rate = drop.getChance();
                    double modifiedChance = rate * rateBonus;
                    modifiedChance = Math.max(modifiedChance, 1);
                    int roll = (int) Math.ceil(modifiedChance);
                    if (World.getWorld().random().nextInt(roll) == 0) {
                        int minimum = Math.max(drop.getMinimumAmount(), 1);
                        int maximum = Math.max(drop.getMaximumAmount(), 1);
                        Item item = new Item(ItemRepository.getItemId(drop.getItem()), World.getWorld().random(minimum, maximum));
                        list.add(item);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public List<Item> simulate(Player player, int kills) {
        List<Item> list = new ArrayList<>();
        if (alwaysDrops != null && !alwaysDrops.isEmpty()) {
            for (var drops : alwaysDrops) {
                list.add(new Item(ItemRepository.getItemId(drops.getItem()), World.getWorld().random(Math.max(drops.getMinimumAmount(), 1), Math.max(drops.getMaximumAmount(), 1))));
            }
        }
        double rateBonus = player.getDropRateBonus() - 1D;
        List<ItemDrop> temp = new ArrayList<>(drops);
        for (int i = 0; i < kills; i++) {
            Collections.shuffle(temp);
            if (!temp.isEmpty()) {
                for (var drop : temp) {
                    int rate = drop.getChance();
                    double modifiedChance = rate * rateBonus;
                    modifiedChance = Math.max(modifiedChance, 1); // Ensure modified chance is at least 1
                    int roll = (int) Math.ceil(modifiedChance);
                    if (World.getWorld().random().nextInt(roll) == 0) { // Generate random number between 0 and modifiedChance - 1
                        int minimum = Math.max(drop.getMinimumAmount(), 1);
                        int maximum = Math.max(drop.getMaximumAmount(), 1);
                        Item item = new Item(ItemRepository.getItemId(drop.getItem()), World.getWorld().random(minimum, maximum));
                        list.add(item);
                        break;
                    }
                }
            }
        }
        return list;
    }
}

