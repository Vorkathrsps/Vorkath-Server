package com.cryptic.model.content.consumables.potions.impl;

import com.cryptic.model.content.EffectTimer;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;

/**
 * @author Origin
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class OverloadPotion {

    public static void apply(Player player) {

        player.putAttrib(AttributeKey.OVERLOAD_TASK_RUNNING, true);

        int overloadTicks = player.getAttribOr(AttributeKey.OVERLOAD_POTION, 0);

        TaskManager.submit(new Task("OverloadTask", 1, false) {

            int ticks = overloadTicks;

            @Override
            protected void execute() {

                if (!player.isRegistered() || player.dead() || ticks == 0 || WildernessArea.inWild(player)|| player.getDueling().inDuel()) {
                    stop();
                    player.healPlayer();
                    player.getSkills().resetStats();
                    player.clearAttrib(AttributeKey.OVERLOAD_TASK_RUNNING);
                    player.getPacketSender().sendEffectTimer(0, EffectTimer.OVERLOAD);
                    player.message("Your overload effect wears off upon entering the wilderness.");
                    return;
                }

                //Every 15 seconds apply effect
                if (ticks % 25 == 0) {
                    player.getSkills().overloadPlusBoost(Skills.ATTACK);
                    player.getSkills().overloadPlusBoost(Skills.STRENGTH);
                    player.getSkills().overloadPlusBoost(Skills.DEFENCE);
                    player.getSkills().overloadPlusBoost(Skills.RANGED);
                    player.getSkills().overloadPlusBoost(Skills.MAGIC);
                }

                if (--ticks > 0) {

                    //System.out.println("Ticks Remaining"+ticks);

                    if (ticks == 0) {
                        stop();
                        player.healPlayer();
                        player.getSkills().resetStats();
                        player.clearAttrib(AttributeKey.OVERLOAD_TASK_RUNNING);
                        player.getPacketSender().sendEffectTimer(0, EffectTimer.OVERLOAD);
                        player.message(Color.RED.tag() + "Your overload potion has expired.");
                    }
                }


            }
        });
    }
}
