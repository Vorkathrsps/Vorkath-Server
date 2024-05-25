package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Utils;

import java.util.Optional;

/**
 * @author Origin
 * april 18, 2020
 */
public class ResetSlayerTask implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 2) {
            player.message("Invalid syntax. Please enter a username.");
            player.message("::resetslayertask username");
            return;
        }
        final String player2 = Utils.formatText(command.substring(parts[0].length() + 1));
        Optional<Player> plr = World.getWorld().getPlayerByName(player2);
        if (plr.isPresent()) {

            SlayerTask slayer = World.getWorld().getSlayerTasks();
            slayer.sendCancelTaskDialouge(player);
            player.message("You have reset the slayer task for player "+plr.get().getUsername()+".");
            plr.get().message("Your slayer task has been reset, talk to any slayer master for a new task.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }

}
