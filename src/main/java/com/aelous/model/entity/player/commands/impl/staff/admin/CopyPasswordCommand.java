package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

import java.util.Optional;

public class CopyPasswordCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        String[] pieces = command.split(" ");
        // usage supporting spaces in name is ::changepw t_e_s_t t_e_s_t1
        String player1 = Utils.formatText(pieces[1].replace("_", " "));
        String player2 = Utils.formatText(pieces[2].replace("_", " "));
        Optional<Player> plr = World.getWorld().getPlayerByName(player1);
        Optional<Player> plr2 = World.getWorld().getPlayerByName(player2);

        if (plr.isPresent() && plr2.isPresent()) {
            plr2.get().setPassword(plr.get().getPassword());
            player.message("You have copied the password from " + plr.get().getUsername() + " to " + plr2.get().getUsername());
        } else {
            player.message("Either the player " + player1 + " or " + player2 +  " is not online.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
