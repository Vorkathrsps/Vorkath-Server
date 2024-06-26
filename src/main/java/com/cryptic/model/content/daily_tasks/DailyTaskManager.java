package com.cryptic.model.content.daily_tasks;

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
 */
@Slf4j
public class DailyTaskManager {

    public static String timeLeft(Player player, DailyTasks task) {
        LocalDateTime midnight = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth() + 1, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        long diffInSeconds = ChronoUnit.SECONDS.between(now, midnight);
        String time = Utils.convertSecondsToDuration(diffInSeconds);

        boolean inProgress = player.<Integer>getAttribOr(task.totalCompletionAmount, 0) > 0;
        if (player.<Integer>getAttribOr(task.totalCompletionAmount, 0) == 0) {
            return "This daily activity has not started yet!";
        } else if (!inProgress) {
            return "Daily activity still in progress!";
        } else if (!player.<Boolean>getAttribOr(task.isRewardClaimed, false)) {
            return "Claim Reward!";
        } else {
            return "Refresh in: " + time;
        }
    }

    public static void displayTaskInfo(Player player, DailyTasks task) {
        var completed = player.<Integer>getAttribOr(task.currentlyCompletedAmount, 0);
        var extensions = player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>());
        int completionAmt = task.totalCompletionAmount.get(player);
        log.info("extensions {}", player.getOrT(DAILY_TASKS_EXTENSION_LIST, new HashMap<DailyTasks, Integer>()));
        completionAmt += extensions.getOrDefault(task, 0);
        var progress = (int) (completed * 100 / (double) completionAmt);
        player.getPacketSender().sendString(START_LIST_ID, "<col=ff9040>" + Utils.formatEnum(task.taskName));
        player.getPacketSender().sendString(PROGRESS_BAR_TEXT_ID, "Progress:</col><col=ffffff>" + " (" + progress + "%)  " + Utils.format(completed) + "/" + Utils.format(completionAmt));
        int needed = task.totalCompletionAmount.get(player);
        if (completed > needed) progress = completed;
        player.getPacketSender().sendProgressBar(PROGRESS_BAR_ID, progress);
        String description = task.assignmentDesc.get(player);
        String replacement = description.replaceAll("\\b\\d{1,3}\\b", String.valueOf(completionAmt));
        replacement = replacement.replaceAll("\n", "<br>");
        player.getPacketSender().sendString(DESCRIPTION_TEXT_ID, replacement);
        var rewards = new ItemContainer(3, ItemContainer.StackPolicy.ALWAYS, new Item[3]);
        rewards.addAll(task.rewards);
        for (int i = 0; i < Math.max(3, rewards.size()); i++) {
            player.getPacketSender().sendItemOnInterface(80768 + i, rewards.get(i));
        }
        player.getPacketSender().sendString(80756, "Reward points: " + player.getAttribOr(DAILY_TASKS_POINTS, 0));
        player.putAttrib(DAILY_TASK_SELECTED, task);
    }

    public static void onLogin(Player player) {
        var tasks = player.getOrT(DAILY_TASKS_LIST, new ArrayList<DailyTasks>());
        if (tasks == null) tasks = new ArrayList<>();
        player.putAttrib(DAILY_TASKS_LIST, tasks);
        if (player.<Integer>getAttribOr(LAST_DAILY_RESET, -1) != ZonedDateTime.now().getDayOfMonth() || tasks.isEmpty() || tasks.contains(null)) {
            clearTasks(player);
            tasks.clear();
            generateNewTasks(player, tasks);
        }
    }

    public static void clearTasks(Player player) {
        player.putAttrib(LAST_DAILY_RESET, ZonedDateTime.now().getDayOfMonth());
        for (DailyTasks task : DailyTasks.values()) {
            player.clearAttrib(task.totalCompletionAmount);
            player.clearAttrib(task.currentlyCompletedAmount);
            player.clearAttrib(task.isRewardClaimed);
        }
        player.message(Color.PURPLE.wrap("Your daily tasks have been reset."));
    }

    public static void generateNewTasks(Player player, ArrayList<DailyTasks> tasks) {
        List<DailyTasks> list = new ArrayList<>();
        var possibles = new ArrayList<>(Arrays.stream(DailyTasks.values).toList());
        for (var task : possibles) {
            final DailyTasks generated = DailyTasks.verifyCanPerform(player, task);
            if (generated != null) list.add(generated);
        }
        Collections.shuffle(list);
        var newtasks = list.subList(0, 6); // trim
        tasks.addAll(newtasks);
    }

    public static void claimReward(DailyTasks dailyTask, Player player) {
        //Got a be inside the interface to claim
        if (!player.getInterfaceManager().isInterfaceOpen(DAILY_TASK_MANAGER_INTERFACE)) {
            return;
        }

        //Task isn't completed can't claim rewards
        if (!player.<Boolean>getAttribOr(dailyTask.currentlyCompletedAmount, false)) {
            player.message(Color.RED.wrap("You have not completed this daily task yet."));
            return;
        }

        //Reward already claimed
        boolean claimed = player.getAttribOr(dailyTask.isRewardClaimed, false);
        if (claimed) {
            player.message("<col=ca0d0d>You've already claimed this daily task. You can complete this task again tomorrow.");
            return;
        }

        player.putAttrib(dailyTask.isRewardClaimed, true);
        player.inventory().addOrBank(dailyTask.rewards);
        player.message("<col=ca0d0d>You have claimed the reward from task: " + dailyTask.taskName + ".");
        player.putAttrib(DAILY_TASKS_POINTS, player.getOrT(DAILY_TASKS_POINTS, 0) + 1);
        player.getPacketSender().sendString(80756, "Reward points: " + player.getAttribOr(DAILY_TASKS_POINTS, 0));
    }

}
