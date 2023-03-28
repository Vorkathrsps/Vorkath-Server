package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.model.entity.attributes.AttributeKey.ASK_FOR_ACCOUNT_PIN;

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
