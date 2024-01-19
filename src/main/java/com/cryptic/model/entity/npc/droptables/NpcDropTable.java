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

    public void postLoad() {
        drops.sort(Comparator.comparingInt(ItemDrop::getChance));
    }

    public List<Item> getDrops(Player player, int rolls) {
        List<Item> list = new ArrayList<>();
        if (!alwaysDrops.isEmpty()) {
            for (var drops : alwaysDrops) {
                list.add(new Item(ItemRepository.getItemId(drops.getItem()), World.getWorld().random(Math.max(drops.getMinimumAmount(), 1), Math.max(drops.getMaximumAmount(), 1))));
            }
        }
        for (int i = 0; i < rolls; i++) {
            if (!drops.isEmpty()) {
                for (var drop : drops) {
                    int rate = drop.getChance();
                    var reduction = rate * player.getDropRateBonus() / 100;
                    rate -= reduction;
                    if (World.getWorld().random(1, rate) == 1) {
                        int minimum = Math.max(drop.getMinimumAmount(), 1);
                        int maximum = Math.max(drop.getMaximumAmount(), 1);
                        list.add(new Item(ItemRepository.getItemId(drop.getItem()), World.getWorld().random(minimum, maximum)));
                    }
                }
            }
        }
        return list;
    }
}

