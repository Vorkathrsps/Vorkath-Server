package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

public class SlayerHelm {

    public static boolean onContainerAction2(Player player, Item item) {
        if (item.name().toLowerCase().contains("slayer helmet") || item.name().toLowerCase().contains("slayer helmet (i)")) {
            Slayer.displayCurrentAssignment(player);
            return true;
        }
        return false;
    }

    public static boolean onItemOption3(Player player, Item item) {
        if (item.name().toLowerCase().contains("slayer helmet") || item.name().toLowerCase().contains("slayer helmet (i)")) {
            Slayer.displayCurrentAssignment(player);
            return true;
        }
        return false;
    }
}
