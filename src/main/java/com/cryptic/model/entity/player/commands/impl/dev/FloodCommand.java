package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.GameServer;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class FloodCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amt = Integer.parseInt(parts[1]);
        GameServer.getFlooder().login(amt);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
