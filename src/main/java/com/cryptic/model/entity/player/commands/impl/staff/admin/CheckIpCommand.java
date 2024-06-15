package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.GameEngine;
import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.services.database.transactions.FindPlayersFromIpDatabaseTransaction;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class CheckIpCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CheckIpCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 1) {
            player.message("Invalid syntax. Please use: ::checkip [username]");
            player.message("Example: ::checkip 127.0.0.1 ");
            return;
        }
        //String IP = parts[1];
        String username = parts[1];
        Optional<Player> other = World.getWorld().getPlayerByName(username);
        if (other.isPresent()) {
            Player p2 = other.get();
            player.message(p2.getUsername() + ": " + Color.MITHRIL.wrap(p2.getHostAddress()));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
