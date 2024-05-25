package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.timers.TimerKey;

public class SaturatedHeart extends PacketInteraction {
    public static boolean onItemOption1(Player player, Item item) {
        if (item.getId() == ItemIdentifiers.SATURATED_HEART) {
            player.getTimers().register(TimerKey.SATURATED_HEART, 500);
            int boost = 4 + (player.getSkills().xpLevel(Skills.MAGIC) / 10);
            player.graphic(2287, GraphicHeight.LOW, 0);
            if (player.getSkills().levels()[Skills.MAGIC] == player.getSkills().xpLevel(Skills.MAGIC)) {
                player.getSkills().alterSkill(Skills.MAGIC, boost);
            }
            return true;
        }
        return false;
    }

}
