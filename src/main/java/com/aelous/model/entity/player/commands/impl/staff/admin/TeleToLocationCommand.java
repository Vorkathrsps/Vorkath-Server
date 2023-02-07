package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

public class TeleToLocationCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = player.tile().getLevel(); // stay on current lvl
        if (parts.length == 4) {
            z = Integer.parseInt(parts[3]);
        }
        player.teleport(new Tile(x, y, z));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
