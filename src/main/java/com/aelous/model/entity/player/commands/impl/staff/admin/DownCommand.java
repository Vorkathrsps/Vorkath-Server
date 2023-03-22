package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class DownCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.teleport(player.tile().x, player.tile().y, Math.max(0, player.tile().level - 1));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
