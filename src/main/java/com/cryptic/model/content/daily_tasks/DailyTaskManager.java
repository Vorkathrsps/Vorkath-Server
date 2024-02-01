package com.cryptic.model.content.daily_tasks;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.ItemContainer;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.cryptic.model.content.daily_tasks.DailyTaskUtility.*;
import static com.cryptic.model.entity.attributes.AttributeKey.*;

/**
 * @author Origin | June, 15, 2021, 16:15
 * 
 */
@Slf4j
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
        var completed = player.<Integer>getAttribOr(task.key, 0);
        var extensions = player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>());
        var completionAmt = task.completionAmount;
        log.info("{}", player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>()));
        completionAmt += extensions.getOrDefault(task, 0);
        final var progress = (int) (completed * 100 / (double) completionAmt);
        player.getPacketSender().sendString(START_LIST_ID, "<col=ff9040>" + Utils.formatEnum(task.taskName));
        player.getPacketSender().sendString(PROGRESS_BAR_TEXT_ID, "Progress:</col><col=ffffff>" + " (" + progress + "%) " + Utils.format(completed) + " / " + Utils.format(completionAmt));
        player.getPacketSender().sendProgressBar(PROGRESS_BAR_ID, progress);
        player.getPacketSender().sendString(DESCRIPTION_TEXT_ID, task.taskDescription);

        //Clear item frames
        var rewards = new ItemContainer(3, ItemContainer.StackPolicy.ALWAYS, new Item[3]);
        rewards.addAll(task.rewards);
        for (int i = 0; i < Math.max(3, rewards.size()); i++) {
            player.getPacketSender().sendItemOnInterface(80768 + i, rewards.get(i));
        }

        player.putAttrib(DAILY_TASK_SELECTED, task);
    }

    public static void increase(DailyTasks dailyTask, Player player) {
        //Can only increase when the task isn't already finished.
        if (dailyTask.canIncrease(player)) {
            var completionAmount = dailyTask.completionAmount;
            var extensions = player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>());
            completionAmount += extensions.getOrDefault(dailyTask, 0);

            var newCompletedAmt = player.<Integer>getAttribOr(dailyTask.key, 0) + 1;
            player.putAttrib(dailyTask.key, newCompletedAmt);
            player.message(Color.PURPLE.wrap("Daily task; " + dailyTask.taskName + " Completed: (" + newCompletedAmt + "/" + completionAmount + ")"));

            //We have completed the task
            if (newCompletedAmt == completionAmount) {
                player.putAttrib(dailyTask.completed, true);
                player.message(Color.PURPLE.wrap(dailyTask.taskName + " completed, you may now claim its rewards!"));
            }
        }
    }

    public static void onLogin(Player player) {
        var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
        if (tasks == null) {
            tasks = new ArrayList<DailyTasks>();
        }
        player.putAttrib(DAILY_TASKS_LIST, tasks);
        if (player.<Integer>getAttribOr(LAST_DAILY_RESET, -1) != ZonedDateTime.now().getDayOfMonth() || tasks.isEmpty()) {
            player.putAttrib(LAST_DAILY_RESET, ZonedDateTime.now().getDayOfMonth());
            for (DailyTasks task : DailyTasks.values()) {
                player.clearAttrib(task.key);
                player.clearAttrib(task.completed);
                player.clearAttrib(task.rewardClaimed);
            }
            player.message(Color.PURPLE.wrap("Your daily tasks have been reset."));
            tasks.clear();
            var possibles = new ArrayList<>(Arrays.stream(DailyTasks.values()).toList());
            Collections.shuffle(possibles);
            var newtasks = possibles.subList(0, 6); // trim
            tasks.addAll(newtasks);
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
        player.putAttrib(DAILY_TASKS_POINTS, player.getOrT(DAILY_TASKS_POINTS, 0) + 1);
        player.getPacketSender().sendString(80756, "Reward points: "+player.getAttribOr(DAILY_TASKS_POINTS, 0));
    }

}
