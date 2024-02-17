package com.cryptic.model.content.skill.impl.slayer.content;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;

public class SlayerHelm {

    public static boolean onContainerAction2(Player player, Item item) {
        if (item.name().toLowerCase().contains("slayer helmet") || item.name().toLowerCase().contains("slayer helmet (i)")) {
            SlayerTask task = World.getWorld().getSlayerTasks();
            SlayerTask assignment = task.getCurrentAssignment(player);
            if (assignment == null) {
                player.message("You currently do not have an assigned Slayer task.");
                return true;
            }
            player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));
            return true;
        }
        return false;
    }

    public static boolean onItemOption3(Player player, Item item) {
        if (item.name().toLowerCase().contains("slayer helmet") || item.name().toLowerCase().contains("slayer helmet (i)")) {
            SlayerTask task = World.getWorld().getSlayerTasks();
            SlayerTask assignment = task.getCurrentAssignment(player);
            if (assignment == null) {
                player.message("You currently do not have an assigned Slayer task.");
                return true;
            }
            player.message(Color.BLUE.wrap("Your current Slayer assignment is: " + assignment.getTaskName() + " - Remaining Amount: " + assignment.getRemainingTaskAmount(player)));
            return true;
        }
        return false;
    }
}
