package com.cryptic.model.content.consumables.potions.impl;

import com.cryptic.model.content.EffectTimer;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

/**
 * @author Origin | November, 28, 2020, 13:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DivineSuperDefencePotion {

    public static void onLogin(Player me) {
        setTimer(me);
    }

    public static void setTimer(Player player) {
        player.putAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TASK_RUNNING, true);
        TaskManager.submit(new Task("DivineSuperDefencePotionTask", 1, false) {

            @Override
            protected void execute() {
                int ticks = player.<Integer>getAttribOr(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TICKS, 0);
                boolean potionEffectActive = player.getAttribOr(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_EFFECT_ACTIVE, false);

                if (!player.isRegistered() || player.dead() || ticks == 0) {
                    stop();
                    player.clearAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TASK_RUNNING);
                    return;
                }

                if (potionEffectActive) {
                    player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(ticks), EffectTimer.DIVINE_SUPER_DEFENCE_POTION);
                    ticks--;
                    player.putAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TICKS, ticks--);
                    if (ticks == 0) {
                        player.putAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TASK_RUNNING, false);
                        player.putAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_EFFECT_ACTIVE, false);
                        player.putAttrib(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TICKS, 0);
                        player.message(Color.RED.tag() + "Your divine super attack potion has expired.");
                        stop();
                    }
                }
            }
        });
    }
}
