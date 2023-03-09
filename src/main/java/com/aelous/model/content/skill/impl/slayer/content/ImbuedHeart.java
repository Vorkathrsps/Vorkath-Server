package com.aelous.model.content.skill.impl.slayer.content;

import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

public class ImbuedHeart {

    public static void activate(Player player) {
        if(!player.inventory().contains(ItemIdentifiers.IMBUED_HEART)) {
            return;
        }

        if (player.getTimers().has(TimerKey.IMBUED_HEART_COOLDOWN)) {
            int ticks = player.getTimers().left(TimerKey.IMBUED_HEART_COOLDOWN);

            if (ticks >= 100) {
                int minutes = ticks / 100;
                player.message("The heart is still drained of its power. Judging by how it feels, it will be ready in around "+minutes+" minutes.");
            } else {
                int seconds = ticks / 10 * 6;
                player.message("The heart is still drained of its power. Judging by how it feels, it will be ready in around "+seconds+" seconds.");
            }
        } else {
            if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_POTIONS.ordinal()]) {
                player.message("Stat-boosting items are disabled for this duel.");
            } else {
                player.message("<col="+Color.RED.getColorValue()+">Your imbued heart has regained its magical power.");
                player.graphic(1316, GraphicHeight.LOW, 30);
                player.getTimers().register(TimerKey.IMBUED_HEART_COOLDOWN, 700);
                int seconds = 700 / 10 * 6;//7 minutes
                player.getPacketSender().sendEffectTimer(seconds, EffectTimer.IMBUED_HEART);

                // This boost will only increment.
                int boost = 1 + (player.getSkills().xpLevel(Skills.MAGIC) / 10);
                if (player.getSkills().levels()[Skills.MAGIC] == player.getSkills().xpLevel(Skills.MAGIC)) {
                    player.getSkills().alterSkill(Skills.MAGIC, boost);
                }
            }
        }
    }
}
