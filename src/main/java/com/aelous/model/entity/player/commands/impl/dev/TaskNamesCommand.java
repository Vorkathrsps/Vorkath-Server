package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskNamesCommand implements Command {

    private static final Logger logger = LogManager.getLogger(TaskNamesCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        logger.info("Pending tasks: " + TaskManager.getPendingTaskNames());
        logger.info("Active tasks: " + TaskManager.getActiveTaskNames());
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
