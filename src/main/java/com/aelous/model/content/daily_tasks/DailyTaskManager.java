package com.aelous.model.content.daily_tasks;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.aelous.model.content.daily_tasks.DailyTaskUtility.*;
import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author Patrick van Elderen | June, 15, 2021, 16:15
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DailyTaskManager {

    public static String timeLeft(Player player, DailyTasks task) {
        LocalDateTime midnight = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth() + 1, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        long diffInSeconds = ChronoUnit.SECONDS.between(now, midnight);
        String time = Utils.convertSecondsToDuration(diffInSeconds);

        boolean inProgress = player.<Integer>getAttribOr(task.key, 0) > 0;
        if (player.<Integer>getAttribOr(task.key, 0) == 0) {
            return "This daily activity has not started yet!";
        } else if (!inProgress) {
            return "Daily activity still in progress!";
        } else if (!player.<Boolean>getAttribOr(task.rewardClaimed, false)) {
            return "Claim Reward!";
        } else {
            return "Refresh in: " + time;
        }
    }

    public static void displayTaskInfo(Player player, DailyTasks task) {
        final var completed = player.<Integer>getAttribOr(task.key, 0);
        final var progress = (int) (completed * 100 / (double) task.completionAmount);
        player.getPacketSender().sendString(START_LIST_ID, "<col=ff9040>" + Utils.formatEnum(task.taskName));
        player.getPacketSender().sendString(PROGRESS_BAR_TEXT_ID, "Progress:</col><col=ffffff>" + " (" + progress + "%) " + Utils.format(completed) + " / " + Utils.format(task.completionAmount));
        player.getPacketSender().sendProgressBar(PROGRESS_BAR_ID, progress);
        player.getPacketSender().sendString(DESCRIPTION_TEXT_ID, task.taskDescription);
        player.getPacketSender().sendString(TIME_FRAME_TEXT_ID, timeLeft(player, task));

        //Clear item frames
        player.getPacketSender().sendItemOnInterface(LEFT_SIDE_REWARD_CONTAINER);
        player.getPacketSender().sendItemOnInterface(RIGHT_SIDE_REWARD_CONTAINER);

        player.getPacketSender().sendItemOnInterface(LEFT_SIDE_REWARD_CONTAINER, Arrays.stream(task.rewards).limit(2).toArray(Item[]::new));
        player.getPacketSender().sendItemOnInterface(RIGHT_SIDE_REWARD_CONTAINER, Arrays.stream(task.rewards).skip(2).toArray(Item[]::new));

        player.putAttrib(DAILY_TASK_SELECTED, task);
    }

    public static void openCategory(Player player, TaskCategory category) {
        final List<DailyTasks> list = DailyTasks.asList(category);

        //Clear text and hide buttons
        for (int i = 41521; i < 41521 + 20; i += 2) {
            player.getPacketSender().sendInterfaceDisplayState(i, true);
            player.getPacketSender().sendString(i + 1, "");
        }

        int base = 41522;
        for (final DailyTasks tasks : list) {
            int completed = player.getAttribOr(tasks.key, 0);
            if (completed > tasks.completionAmount) {
                completed = tasks.completionAmount;
            }
            int totalAmount = tasks.completionAmount;

            player.getPacketSender().sendInterfaceDisplayState(base - 1, false);
            player.getPacketSender().sendString(base, "" + color(completed, totalAmount) + tasks.taskName);
            base += 2;
        }
    }

    public static void increase(DailyTasks dailyTask, Player player) {
        //Can only increase when the task isn't already finished.
        if (dailyTask.canIncrease(player)) {
            var completionAmount = dailyTask.completionAmount;
            var increase = player.<Integer>getAttribOr(dailyTask.key, 0) + 1;
            player.putAttrib(dailyTask.key, increase);
            player.message(Color.PURPLE.wrap("Daily task; " + dailyTask.taskName + " Completed: (" + increase + "/" + completionAmount + ")"));

            //We have completed the task
            if (increase == dailyTask.completionAmount) {
                player.putAttrib(dailyTask.completed, true);
                player.message(Color.PURPLE.wrap(dailyTask.taskName + " completed, you may now claim its rewards!"));
            }
        }
    }

    public static void onLogin(Player player) {
        if (player.<Integer>getAttribOr(LAST_DAILY_RESET, -1) != ZonedDateTime.now().getDayOfMonth()) {
            player.putAttrib(LAST_DAILY_RESET, ZonedDateTime.now().getDayOfMonth());
            for (DailyTasks task : DailyTasks.values()) {
                player.clearAttrib(task.key);
                player.clearAttrib(task.completed);
                player.clearAttrib(task.rewardClaimed);
            }
            player.message(Color.PURPLE.wrap("Your daily tasks have been reset."));
        }
    }//what is the name about pvp mode?

    public static void claimReward(DailyTasks dailyTask, Player player) {
        //Got a be inside the interface to claim
        if (!player.getInterfaceManager().isInterfaceOpen(DAILY_TASK_MANAGER_INTERFACE)) {
            return;
        }

        //Task isn't completed can't claim rewards
        if (!player.<Boolean>getAttribOr(dailyTask.completed, false)) {
            player.message(Color.RED.wrap("You have not completed this daily task yet."));
            return;
        }

        //Reward already claimed
        boolean claimed = player.getAttribOr(dailyTask.rewardClaimed, false);
        if (claimed) {
            player.message("<col=ca0d0d>You've already claimed this daily task. You can complete this task again tomorrow.");
            return;
        }

        player.putAttrib(dailyTask.rewardClaimed, true);
        player.inventory().addOrBank(dailyTask.rewards);
        player.message("<col=ca0d0d>You have claimed the reward from task: " + dailyTask.taskName + ".");
    }

    public static void pvpTasks(Player player) {
        openCategory(player, TaskCategory.PVP);
        displayTaskInfo(player, DailyTasks.BOTS);
        player.putAttrib(AttributeKey.DAILY_TASK_CATEGORY, TaskCategory.PVP);
        player.getInterfaceManager().open(DAILY_TASK_MANAGER_INTERFACE);
    }

    public static void pvmTasks(Player player) {
        openCategory(player, TaskCategory.PVM);
        displayTaskInfo(player, DailyTasks.BATTLE_MAGE);
        player.putAttrib(AttributeKey.DAILY_TASK_CATEGORY, TaskCategory.PVM);
        player.getInterfaceManager().open(DAILY_TASK_MANAGER_INTERFACE);
    }

    public static void otherTasks(Player player) {
        openCategory(player, TaskCategory.OTHER);
        displayTaskInfo(player, DailyTasks.WILDY_AGLITY);
        player.putAttrib(AttributeKey.DAILY_TASK_CATEGORY, TaskCategory.OTHER);
        player.getInterfaceManager().open(DAILY_TASK_MANAGER_INTERFACE);
    }

    private static String color(int amount, int max) {
        if (amount == 0) {
            return "<col=" + Color.RED.getColorValue() + ">";
        }
        if (amount >= max) {
            return "<col=" + Color.GREEN.getColorValue() + ">";
        }
        return "<col=" + Color.ORANGE.getColorValue() + ">";
    }
}
