package com.aelous.model.entity.player.commands.impl.owner;

import com.aelous.GameServer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetStaffOnlyLoginCommand implements Command {
    private static final Logger logger = LogManager.getLogger(SetStaffOnlyLoginCommand.class);
    @Override
    public void execute(Player player, String command, String[] parts) {
        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (parts.length < 2) {
            player.message("Set admin only login usage: ::setadminonlylogin 0-1");
            return;
        }
        String[] pieces = command.split(" ");
        String status = pieces[1];
        switch (status) {
            case "on", "enable", "1" -> {
                GameServer.setStaffOnlyLogins(true);
                player.message("You have now enabled staff only logins.");
                player.message("Please remember to ::kickall if necessary.");
                logger.info("Staff only login has been enabled by " + player.getUsername());
            }
            default -> {
                GameServer.setStaffOnlyLogins(false);
                player.message("You have now disabled staff only logins.");
                player.message("Please remember to ::kickall if necessary.");
                logger.info("Staff only login has been disabled by " + player.getUsername());
            }
        }

    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isOwner(player);
    }
}
