package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.GameServer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class FloodCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amt = Integer.parseInt(parts[1]);
        GameServer.getFlooder().login(amt);
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isAdministrator(player));
    }

}
