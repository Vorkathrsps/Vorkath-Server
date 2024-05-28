package com.cryptic.model.content.daily_tasks;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.cryptic.model.content.daily_tasks.DailyTaskUtility.*;
import static com.cryptic.model.entity.attributes.AttributeKey.*;

/**
 * @author Origin | June, 15, 2021, 16:05
 */
@Slf4j
public class DailyTaskButtons extends PacketInteraction {

    public static boolean REWARDS_DISABLED = false;

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button == CLAIM_BUTTON_ID) {
            DailyTasks task = player.getAttrib(DAILY_TASK_SELECTED);
            if (task != null) {
                if (!REWARDS_DISABLED) {
                    DailyTaskManager.claimReward(task, player);
                } else {
                    player.message(Color.RED.wrap("You cannot claim the reward at this time, the rewards are disabled until further notice."));
                }
            }
            return true;
        }

        if (button == CLOSE_BUTTON) {
            player.getInterfaceManager().close();
            return true;
        }

        if (button >= 80777 && button <= 80787) {
            var task = button - 80777;
            if (task > 0)
                task /= 2;
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            log.info("btn {} is{}", task, tasks.get(task));
            DailyTaskManager.displayTaskInfo(player, tasks.get(task));
            return true;
        }
        if (button == 80773) { // reroll
            if (!player.getInventory().contains(995, 5_000_000)) {
                player.message(Color.RED.wrap("You do not have enough coins to re-roll your Daily Task."));
                return false;
            }
            var selected = DAILY_TASK_SELECTED.<DailyTasks>get(player);
            if (selected == null) {
                player.message("You need to pick a task to replace.");
                return true;
            }
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            var list = new ArrayList<>(Arrays.stream(DailyTasks.values()).toList());
            Collections.shuffle(list);
            for (int i = 0; i < 6; i++) {
                if (selected != tasks.get(i)) continue;
                tasks.forEach(list::remove);
                tasks.set(i, list.get(i));
                player.getPacketSender().sendString(80778 + (i * 2), tasks.get(i).taskName);
                player.message(Color.ORANGE.wrap("<lsprite=2014><shad>New task: " + tasks.get(i).taskName + "</shad></img>"));
                DailyTaskManager.displayTaskInfo(player, tasks.get(i));
                break;
            }
            player.getInventory().remove(995, 5_000_000);
        }
        if (button == 80775) { // extend tasks
            if (!player.getInventory().contains(995, 10_000_000)) {
                player.message(Color.RED.wrap("You do not have enough coins to re-roll your Daily Task."));
                return false;
            }
            var selected = DAILY_TASK_SELECTED.<DailyTasks>get(player);
            if (selected == null) {
                player.message("You need to pick a task to replace.");
                return true;
            }
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            for (int i = 0; i < 6; i++) {
                if (selected == tasks.get(i)) {
                    var toAdd = (int) (selected.maximumAmt * .25);
                    var extensions = player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>());
                    var newTotal = selected.maximumAmt + extensions.compute(selected, (_, v) -> {
                        if (v == null)
                            v = toAdd;
                        else
                            v += toAdd;
                        return v;
                    });
                    player.message(Color.ORANGE.wrap("<lsprite=2014><shad>Your task has been extended to: " + newTotal + "</shad></img>"));
                    DailyTaskManager.displayTaskInfo(player, tasks.get(i));
                    break;
                }
            }
            player.getInventory().remove(995, 10_000_000);
        }
        return false;
    }
}
