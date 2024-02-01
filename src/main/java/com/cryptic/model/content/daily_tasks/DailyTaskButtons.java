package com.cryptic.model.content.daily_tasks;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.cryptic.model.content.daily_tasks.DailyTaskUtility.*;
import static com.cryptic.model.entity.attributes.AttributeKey.*;

/**
 * @author Origin | June, 15, 2021, 16:05
 * 
 */
@Slf4j
public class DailyTaskButtons extends PacketInteraction {

    public static boolean REWARDS_DISABLED = false;

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button == CLAIM_BUTTON_ID) {
            DailyTasks task = player.getAttrib(DAILY_TASK_SELECTED);
            if (task != null) {
                if(!REWARDS_DISABLED) {
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
            log.info("{}", task);
            DailyTaskManager.displayTaskInfo(player, tasks.get(task));
            return true;
        }
        if (button == 80773) { // reroll
            var selected = DAILY_TASK_SELECTED.<DailyTasks>get(player);
            if (selected == null) {
                player.message("You need to pick a task to replace.");
                return true;
            }
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            for (int i = 0; i < 6; i++) {
                if (selected == tasks.get(i)) {
                    var possibles = new ArrayList<>(Arrays.stream(DailyTasks.values()).toList());
                    Collections.shuffle(possibles);
                    tasks.forEach(possibles::remove);
                    tasks.set(i, possibles.get(0));
                    player.message("New task: "+tasks.get(i).taskName);
                    break;
                }
            }
        }
        if (button == 80775) { // extend tasks
            var selected = DAILY_TASK_SELECTED.<DailyTasks>get(player);
            if (selected == null) {
                player.message("You need to pick a task to replace.");
                return true;
            }
            var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
            for (int i = 0; i < 6; i++) {
                if (selected == tasks.get(i)) {
                    var toAdd = (int) (selected.completionAmount * .25);
                    var extensions = player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>());
                    var newTotal = selected.completionAmount + extensions.compute(selected, (k, v) -> {
                        if (v == null)
                            v = toAdd;
                        else
                            v += toAdd;
                        return v;
                    });
                    player.message("New total: "+newTotal);
                    DailyTaskManager.displayTaskInfo(player, selected);
                    break;
                }
            }
        }
        return false;
    }
}
