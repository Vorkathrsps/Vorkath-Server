package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.GameEngine;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class CheckBankCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CheckBankCommand.class);
    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 2) {
            player.message("Invalid use of command.");
            player.message("Use: ::checkbank username");
            return;
        }
        final String player2 = Utils.formatText(command.substring(parts[0].length() + 1));

        Optional<Player> plr = World.getWorld().getPlayerByName(player2);
        if (plr.isPresent()) {
            player.message("The bank for " + player2 + " has been send to the VPS logs, ask Patrick or Supra for the log.");
            logger.info("Bank for "+player2+" is: "+plr.get().getBank().toString());
        } else {
            Player plr2 = new Player();
            plr2.setUsername(Utils.formatText(player2.substring(0, 1).toUpperCase() + player2.substring(1)));
            GameEngine.getInstance().submitLowPriority(() -> {
                try {
                    if (PlayerSave.loadOfflineWithoutPassword(plr2)) {
                        GameEngine.getInstance().addSyncTask(() -> {
                            plr2.auditTabs();
                            player.message("The bank for " + player2 + " has been send to the VPS logs, ask Patrick or Supra for the log.");
                            logger.info("Bank for offline player: "+plr2+" is: "+plr2.getBank().toString());
                        });
                    } else {
                        player.message("The Player " + plr2.getUsername() + " does not exist.");
                    }
                } catch (Exception e) {
                    logger.catching(e);
                }
            });
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
