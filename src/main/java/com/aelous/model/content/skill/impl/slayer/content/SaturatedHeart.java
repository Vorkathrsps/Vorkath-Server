package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

public class SaturatedHeart extends PacketInteraction {
    public static boolean onItemOption1(Player player, Item item) {
        if (item.getId() == ItemIdentifiers.SATURATED_HEART) {
            player.getTimers().register(TimerKey.SATURATED_HEART, 500);
            int boost = 4 + (player.getSkills().xpLevel(Skills.MAGIC) / 10);
            if (player.getSkills().levels()[Skills.MAGIC] == player.getSkills().xpLevel(Skills.MAGIC)) {
                player.getSkills().alterSkill(Skills.MAGIC, boost);
            }
            return true;
        }
        return false;
    }

}
