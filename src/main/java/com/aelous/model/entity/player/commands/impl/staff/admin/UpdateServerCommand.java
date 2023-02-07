package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.GameServer;
import com.aelous.GameEngine;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickableTask;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class UpdateServerCommand implements Command {

    private static final Logger logger = LogManager.getLogger(UpdateServerCommand.class);

    public static int time = 1_000;
    public static Task updateTask = null;

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 2) {
            player.message("Cannot update without specifying time.");
            return;
        }

        try {
            time = Integer.parseInt(parts[1]);
            logger.info("An update initiated by {} to happen in {} ticks.", player.getUsername(), time);
            Utils.sendDiscordInfoLog("An update initiated by "+player.getUsername()+" to happen in "+time+" ticks.");
            GameServer.setUpdating(true);

            // Warn all players about the scheduled update.
            World.getWorld().getPlayers().stream().filter(Objects::nonNull).forEach(p -> p.getPacketSender().sendSystemUpdate(time));

            if (time == 0) {
                // cancel
                GameServer.setUpdating(false);
                updateTask = null;
                return;
            }
            if (updateTask == null) {
                // maintain 1 instance only
                updateTask = new TickableTask() {
                    @Override
                    protected void tick() {
                        if (UpdateServerCommand.updateTask == null) {
                            // its been cancelled
                            stop();
                            return;
                        }
                        if (UpdateServerCommand.time-- == 0) {
                            logger.info("Enter task shutdown server.");
                            Utils.sendDiscordInfoLog("Enter task shutdown server.");
                            GameEngine.getInstance().shutdown();
                            stop();
                        }
                    }
                };
                TaskManager.submit(updateTask);
            }
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
