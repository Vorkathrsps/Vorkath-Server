package com.cryptic.model.content.consumables.potions.impl;

import com.cryptic.model.content.EffectTimer;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

/**
 * @author Origin | January, 25, 2021, 10:36
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DivineBastionPotion {

    public static void onLogin(Player me) {
        setTimer(me);
    }

    public static void setTimer(Player player) {
        player.putAttrib(AttributeKey.DIVINE_BASTION_POTION_TASK_RUNNING, true);
        TaskManager.submit(new Task("DivineBastionPotionTask", 1, false) {

            @Override
            protected void execute() {
                int ticks = player.<Integer>getAttribOr(AttributeKey.DIVINE_BASTION_POTION_TICKS, 0);
                boolean potionEffectActive = player.getAttribOr(AttributeKey.DIVINE_BASTION_POTION_EFFECT_ACTIVE, false);

                if(!player.isRegistered() || player.dead() || ticks == 0) {
                    stop();
                    player.clearAttrib(AttributeKey.DIVINE_BASTION_POTION_TASK_RUNNING);
                    return;
                }

                if(potionEffectActive) {
                    player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(ticks), EffectTimer.DIVINE_BASTION_POTION);
                    ticks--;
                    player.putAttrib(AttributeKey.DIVINE_BASTION_POTION_TICKS, ticks--);
                    if(ticks == 0) {
                        player.putAttrib(AttributeKey.DIVINE_BASTION_POTION_TASK_RUNNING, false);
                        player.putAttrib(AttributeKey.DIVINE_BASTION_POTION_EFFECT_ACTIVE, false);
                        player.putAttrib(AttributeKey.DIVINE_BASTION_POTION_TICKS, 0);
                        player.message(Color.RED.tag() + "Your divine bastion potion has expired.");
                        stop();
                    }
                }
            }
        });
    }
}
