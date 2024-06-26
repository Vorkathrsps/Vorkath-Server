package com.cryptic.model.content.skill;

import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a default skillable.
 * A "default" skill is where the player simply animates
 * until a set amount of cycles have passed, and then
 * is rewarded with items.
 *
 * @author Professor Oak
 *
 */
public abstract class DefaultSkillable implements Skillable {

    /**
     * The {@link Task}s which is used to process
     * the skill.
     */
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void start(Player player) {
        startAnimationLoop(player);

        Task task = new Task("SkillableProcessTask", 1, player, true) {
            int cycle = 0;
            @Override
            protected void execute() {
                if (loopRequirements()) {
                    if (!hasRequirements(player)) {
                        cancel(player);
                        return;
                    }
                }

                onCycle(player);

                if (cycle++ >= cyclesRequired(player)) {
                    finishedCycle(player);
                    cycle = 0;
                }
            }
        };

        TaskManager.submit(task);

        tasks.add(task);
    }

    @Override
    public void cancel(Player player) {
        //Stop all tasks..
        Iterator<Task> i = tasks.iterator();
        while (i.hasNext()) {
            Task task = i.next();
            task.stop();
            i.remove();
        }

        //Reset animation..
        player.animate(Animation.DEFAULT_RESET_ANIMATION);
    }

    @Override
    public boolean hasRequirements(Player player) {
        //Check inventory slots..
        if (!allowFullInventory()) {
            if (player.inventory().getFreeSlots() == 0) {
                player.message("Not enough space in your inventory.");
                return false;
            }
        }

        //Check if busy..
        return !player.busy();
    }

    @Override
    public void onCycle(Player player) {
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public abstract boolean loopRequirements();
    public abstract boolean allowFullInventory();
}
