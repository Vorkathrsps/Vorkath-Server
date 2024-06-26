package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Origin | June, 28, 2021, 10:41
 * 
 */
public class CheckMultiLoggers implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        List<String> names = new ArrayList<>();

        if (parts.length < 2) {
            player.message("Invalid syntax. Please use: ::checkmulti [name]");
            return;
        }

        String username = Utils.formatText(command.substring(11)); // after "checkmulti "
        Optional<Player> playerToCheck = World.getWorld().getPlayerByName(username);
        if(playerToCheck.isEmpty()) {
            player.message(Color.RED.wrap(username+" is not online."));
            return;
        }

        World.getWorld().getPlayers().forEach(p -> {
            //When the player matches the IP of the player being checked add the names to a list.
            if(p.getHostAddress().equalsIgnoreCase(playerToCheck.get().getHostAddress())) {
                names.add(p.getUsername()+" in wild: "+ WildernessArea.inWilderness(p.tile()));
            }
        });

        player.message("Accounts online: " + names.toString());
        //Clear list now
        names.clear();
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isSupport(player);
    }
}
