package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskAmountCommand implements Command {

    private static final Logger logger = LogManager.getLogger(TaskAmountCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        logger.info("Tasks with the name " + parts[1] + ": " + TaskManager.getTaskAmountByName(parts[1]));
        player.message("Tasks with the name " + parts[1] + ": " + TaskManager.getTaskAmountByName(parts[1]));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
