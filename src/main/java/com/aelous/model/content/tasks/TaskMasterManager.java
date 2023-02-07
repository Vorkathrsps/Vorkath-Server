package com.aelous.model.content.tasks;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.content.tasks.rewards.TaskReward;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author Patrick van Elderen | April, 08, 2021, 21:55
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TaskMasterManager {

    private final Player player;

    public TaskMasterManager(Player player) {
        this.player = player;
    }

    public void increase(Tasks taskToIncrease) {
        increase(taskToIncrease,1);
    }

    /**
     * Activates, increases the task and also handles completion.
     *
     * @param taskToIncrease The task
     */
    public void increase(Tasks taskToIncrease, int increaseBy) {
        // Safety checks
        if (taskToIncrease == null) {
            return;
        }

        Tasks task = player.getAttribOr(AttributeKey.TASK, Tasks.NONE);
        if(task == null) return;

        if(task == Tasks.NONE) {
            return;
        }

        if(task == taskToIncrease) {

            int before = player.<Integer>getAttribOr(TASK_AMOUNT, 0);

            var current = player.<Integer>getAttribOr(TASK_AMOUNT, 0) + increaseBy;
            var completeAmount = player.<Integer>getAttribOr(TASK_COMPLETE_AMOUNT, 0);
            if (current >= completeAmount)
                current = completeAmount;

            player.putAttrib(TASK_AMOUNT, current);

            int after = player.<Integer>getAttribOr(TASK_AMOUNT, 0);

            if (after != before) {
                if (task.isPvpTask() || task.isPvmTask()) {
                    var taskType = task.isPvpTask() ? "PVP" : "PVM";
                    player.message(Color.GRASS.wrap("Your kill counts towards your " + Color.BLUE.wrap(taskType) + " <col=03700C>task. Kills left: <col=A30072>" + current + "</col> <col=03700C>/ <col=A30072>" + completeAmount + "</col><col=03700C>."));
                } else if (task.isSkillingTask()) {
                    player.message(Color.GRASS.wrap("This actions counts towards your " + Color.BLUE.wrap("skilling") + " <col=03700C>task. Actions left: <col=A30072>" + current + "</col> <col=03700C>/ <col=A30072>" + completeAmount + "</col><col=03700C>."));
                }

                //Task completed
                if (after >= completeAmount) {
                    player.message("You've completed your task, you can now claim your reward!");
                    int tasks_completed = (Integer) player.getAttribOr(TASKS_COMPLETED, 0) + 1;
                    player.putAttrib(TASKS_COMPLETED, tasks_completed);

                    player.message("You have now completed <col=" + Color.BLUE.getColorValue() + ">" + tasks_completed + "</col> tasks.");
                    player.putAttrib(CAN_CLAIM_TASK_REWARD, true);
                }
            }
        }
    }

    public void resetTask() {
        player.putAttrib(TASK, Tasks.NONE);
        player.putAttrib(TASK_AMOUNT,0);
        player.putAttrib(TASK_COMPLETE_AMOUNT,0);
        player.message(Color.RED.wrap("Your task has been reset."));
    }

    public boolean hasTask() {
        Tasks task = player.getAttribOr(TASK, Tasks.NONE);
        return task != Tasks.NONE;
    }

    public void giveTask(boolean pvpTask, boolean skillingTask, boolean pvmTask) {
        //Safety, check if a player already has a task.
        Tasks task = player.getAttribOr(TASK, Tasks.NONE);
        if (hasTask()) {
            return;
        }

        //Randomize a task
        Tasks randomTask = Tasks.NONE;

        if(pvpTask) {
            randomTask = Tasks.randomPVPTask();
        } else if(skillingTask) {
            randomTask = Tasks.randomSkillingTask();
        } else if(pvmTask) {
            randomTask = Tasks.randomPVMTask();
        }

        if(randomTask != null) {
            //Save the enum type
            player.putAttrib(TASK, randomTask);
            player.putAttrib(TASK_AMOUNT, 0);
            player.putAttrib(TASK_COMPLETE_AMOUNT, randomTask.getTaskAmount());
        }
    }

    public void open() {
        Tasks task = player.getAttribOr(TASK, Tasks.NONE);
        if(task == null) {
            player.message("You have no task.");
            return;
        }

        player.getInterfaceManager().open(54731);
        player.getPacketSender().sendString(54733, "Task Manager");

        var completed = player.<Integer>getAttribOr(TASK_AMOUNT, 0);
        var completionAmount = player.<Integer>getAttribOr(TASK_COMPLETE_AMOUNT, 0);
        var progress = (int) (completed * 100 / (double) completionAmount);
        player.getPacketSender().sendString(54762, "(" + progress + "%) (" + completed + "/" + completionAmount + ")");
        player.getPacketSender().sendProgressBar(54760, progress);

        player.getPacketSender().sendItemOnInterface(54759, TaskReward.getPossibleRewards());

        for (int i = 54738; i < 54758; i++) {
            player.getPacketSender().sendString(i, "");//Clear old
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (task.getTaskRequirements() != null) {
            stringBuilder.append("Requirement:<br>");
            for (String s : task.getTaskRequirements()) {
                stringBuilder.append(s).append("<br>");
            }

            stringBuilder.append("<br><br>Task(s)").append("<br>- ").append(task.task());
        }

        player.getPacketSender().sendString(54738, stringBuilder.toString());
    }

    public void claimReward() {
        if(!hasTask()) {
            player.message("There are no rewards pending, you have no task.");
            return;
        }

        boolean canClaimReward = player.getAttribOr(CAN_CLAIM_TASK_REWARD, false);

        if (!canClaimReward) {
            final int completed = player.getAttribOr(TASK_AMOUNT, 0);
            final int completeAmt = player.getAttribOr(TASK_COMPLETE_AMOUNT,0);
            player.message("Your task isn't finished yet, you still have to complete (" + Utils.format(completed) + "/" + completeAmt + ").");
            return;
        }
        player.putAttrib(CAN_CLAIM_TASK_REWARD,false);

        TaskReward.reward(player);

        AchievementsManager.activate(player, Achievements.TASK_MASTER_I, 1);
        AchievementsManager.activate(player, Achievements.TASK_MASTER_II, 1);
        AchievementsManager.activate(player, Achievements.TASK_MASTER_III, 1);
        player.getInterfaceManager().close();

        //Reset old task
        player.putAttrib(TASK_AMOUNT, 0);
        player.putAttrib(TASK_COMPLETE_AMOUNT, 0);
        player.putAttrib(TASK, Tasks.NONE);
    }
}
