package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class KillCommand implements Command {
    private static final Logger logger = LogManager.getLogger(KillCommand.class);
    @Override
    public void execute(Player player, String command, String[] parts) {
        Optional<Player> plr = World.getWorld().getPlayerByName(parts.length > 1 ? Utils.formatText(command.substring(parts[0].length() + 1)) : "");
        if (plr.isPresent()) {
            logger.info("Player " + plr.get().getUsername() + " has been killed by admin: " + player.getUsername());
            plr.get().setHitpoints(0);
        } else if (parts.length < 2) {
            logger.info("Player " + player.getUsername() + " has been killed by admin: " + player.getUsername());
            player.setHitpoints(0);
        } else {
            player.message("The player " + Utils.formatText(parts[1]) +  " is not online.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
