package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.GameServer;
import com.aelous.services.database.transactions.RetrievePlayerPasswordDatabaseTransaction;
import com.aelous.GameEngine;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.utility.Utils;

import java.util.Optional;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:40
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class UpdatePasswordCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (parts.length < 2) {
            player.message("Update password usage: ::updatepassword username");
            return;
        }
        String[] pieces = command.split(" ");
        final String player2 = Utils.formatText(command.substring(parts[0].length() + 1));
        String username = player2;
        Optional<Player> plr = World.getWorld().getPlayerByName(player2);
        if (plr.isPresent()) {
            player.message("The player " + username + " is currently online, please try again later.");
            return;
        } else {
            if (!GameServer.properties().enableSql) {
                player.message("Updating passwords is not currently enabled, please try again later.");
                return;
            }
            Player plr2 = new Player();
            plr2.setUsername(username);
            GameServer.getDatabaseService().submit(new RetrievePlayerPasswordDatabaseTransaction(username), password -> {
                GameEngine.getInstance().submitLowPriority(() -> {
                    try {
                        if (PlayerSave.loadOffline(plr2, password)) {
                            PlayerSave.save(plr2);
                            player.message("You have changed the password of offline player " + username);
                        } else {
                            player.message("Something went wrong changing the password of offline player " + username);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }

}
