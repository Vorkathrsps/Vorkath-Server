package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;

import java.util.List;

public class PlayersOnlineCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        List<Player> players = World.getWorld().getPlayers().stream().toList();
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
