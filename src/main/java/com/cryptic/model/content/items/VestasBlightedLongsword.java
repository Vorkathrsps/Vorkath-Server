package com.cryptic.model.content.items;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;

public class VestasBlightedLongsword {

    public static boolean onItemOption1(Player player, Item item) {
        if(item.getId() == ItemIdentifiers.VESTAS_LONGSWORD_INACTIVE) {
            if (player.hp() > 1 && !player.getTimers().has(TimerKey.EAT_ROCKCAKE)) {
                player.hit(null,1);
                player.getTimers().extendOrRegister(TimerKey.EAT_ROCKCAKE, 1);
            }
            return true;
        }
        return false;
    }

}
