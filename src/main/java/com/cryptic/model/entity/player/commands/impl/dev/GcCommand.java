package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class GcCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        System.gc();
        player.message("Garbage collection performed.");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }
}
