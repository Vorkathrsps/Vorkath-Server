package com.cryptic.model.entity.npc.droptables;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public int totalDrops() {
        return alwaysDrops.size() + drops.size();
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
                    var reduction = rate * player.getDropRateBonus() / 150;
                    rate -= reduction;
                    if (Utils.random(1, rate) == 1) {
                        int minimum = Math.max(drop.getMinimumAmount(), 1);
                        int maximum = Math.max(drop.getMaximumAmount(), 1);
                        list.add(new Item(ItemRepository.getItemId(drop.getItem()), Utils.random(minimum, maximum)));
                    }
                }
            }
        }
        return list;
    }
}

