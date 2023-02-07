package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Color;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersOnlineCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        List<Player> players = World.getWorld().getPlayers().stream().collect(Collectors.toList());
        StringBuilder playersOnline = new StringBuilder("<col=800000>Players:");

        for (Player p : players) {
            if(p == null) continue;

            if(player.getPlayerRights().isAdministrator(player)) {
                playersOnline.append("<br><br> - ").append(p.getUsername()).append(" tile: ").append(p.tile());
            } else {
                playersOnline.append("<br><br> - ").append(p.getUsername());
            }
        }
        player.sendScroll(Color.MAROON.wrap("Players online: "+World.getWorld().getPlayers().size()), playersOnline.toString());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
