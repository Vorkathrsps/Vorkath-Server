package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class DismissBroadcastCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        World.getWorld().sendWorldMessage("dismissbroadcast##");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
