package com.aelous.model.content.items;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

public class VestasBlightedLongsword {

    public static boolean onItemOption1(Player player, Item item) {
        if(item.getId() == ItemIdentifiers.VESTAS_LONGSWORD_INACTIVE) {
            if (player.hp() > 1 && !player.getTimers().has(TimerKey.EAT_ROCKCAKE)) {

                player.hit(player,1);
                player.getTimers().extendOrRegister(TimerKey.EAT_ROCKCAKE, 1);
            }
            return true;
        }
        return false;
    }

}
