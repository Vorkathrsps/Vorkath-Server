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
        if (!alwaysDrops.isEmpty()) {
            for (var drops : alwaysDrops) {
                list.add(new Item(ItemRepository.getItemId(drops.getItem()), World.getWorld().random(Math.max(drops.getMinimumAmount(), 1), Math.max(drops.getMaximumAmount(), 1))));
            }
        }
        double rateBonus = 1D - (player.getDropRateBonus() / 100D);
        List<ItemDrop> temp = new ArrayList<>(drops);
        Collections.shuffle(temp);
        for (int i = 0; i < this.rolls; i++) {
            if (!temp.isEmpty()) {
                for (var drop : temp) {
                    if (World.getWorld().random().nextInt((int) Math.ceil(drop.getChance() * rateBonus)) == 1) {
                        int minimum = Math.max(drop.getMinimumAmount(), 1);
                        int maximum = Math.max(drop.getMaximumAmount(), 1);
                        list.add(new Item(ItemRepository.getItemId(drop.getItem()), World.getWorld().random(minimum, maximum)));
                        break;
                    }
                }
            }
        }
        return list;
    }
}

