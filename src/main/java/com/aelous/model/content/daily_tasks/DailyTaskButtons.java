package com.aelous.model.content.daily_tasks;

import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;

import java.util.HashMap;

import static com.aelous.model.content.daily_tasks.DailyTaskUtility.*;
import static com.aelous.model.entity.attributes.AttributeKey.DAILY_TASK_CATEGORY;
import static com.aelous.model.entity.attributes.AttributeKey.DAILY_TASK_SELECTED;

/**
 * @author Patrick van Elderen | June, 15, 2021, 16:05
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DailyTaskButtons extends PacketInteraction {

    public static boolean REWARDS_DISABLED = false;

    private static final HashMap<Integer, DailyTasks> PVP_TASKS_BUTTONS = new HashMap<>();
    private static final HashMap<Integer, DailyTasks> PVM_TASKS_BUTTONS = new HashMap<>();
    private static final HashMap<Integer, DailyTasks> OTHER_TASKS_BUTTONS = new HashMap<>();

    static {
        int button;
        button = 41519;

        for (final DailyTasks dailyTasks : DailyTasks.asList(TaskCategory.PVP)) {
            PVP_TASKS_BUTTONS.put(button += 2, dailyTasks);
        }
        button = 41519;
        for (final DailyTasks dailyTasks : DailyTasks.asList(TaskCategory.PVM)) {
            PVM_TASKS_BUTTONS.put(button += 2, dailyTasks);
        }
        button = 41519;
        for (final DailyTasks dailyTasks : DailyTasks.asList(TaskCategory.OTHER)) {
            OTHER_TASKS_BUTTONS.put(button += 2, dailyTasks);
        }
    }

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (button == PVP_TASKS_BUTTON_ID || button == DAILY_MONEY_MAKER_NORMAL_SPELLBOOK_ID || button == DAILY_MONEY_MAKER_ANCIENT_SPELLBOOK_ID || button == DAILY_MONEY_MAKER_LUNAR_SPELLBOOK_ID) {
            DailyTaskManager.pvpTasks(player);
            return true;
        }

        if (button == PVM_TASKS_BUTTON_ID) {
            DailyTaskManager.pvmTasks(player);
            return true;
        }

        if (button == OTHER_TASKS_BUTTON_ID) {
            DailyTaskManager.otherTasks(player);
            return true;
        }

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
            player.clearAttrib(DAILY_TASK_CATEGORY);
            player.clearAttrib(DAILY_TASK_SELECTED);
            return true;
        }

        //PvP task buttons
        if (player.getAttribOr(DAILY_TASK_CATEGORY, null) == TaskCategory.PVP && PVP_TASKS_BUTTONS.containsKey(button)) {
            DailyTaskManager.displayTaskInfo(player, PVP_TASKS_BUTTONS.get(button));
            return true;
        }

        //PvM task buttons
        if (player.getAttribOr(DAILY_TASK_CATEGORY, null) == TaskCategory.PVM && PVM_TASKS_BUTTONS.containsKey(button)) {
            DailyTaskManager.displayTaskInfo(player, PVM_TASKS_BUTTONS.get(button));
            return true;
        }

        //Other task buttons
        if (player.getAttribOr(DAILY_TASK_CATEGORY, null) == TaskCategory.OTHER && OTHER_TASKS_BUTTONS.containsKey(button)) {
            DailyTaskManager.displayTaskInfo(player, OTHER_TASKS_BUTTONS.get(button));
            return true;
        }
        return false;
    }
}
