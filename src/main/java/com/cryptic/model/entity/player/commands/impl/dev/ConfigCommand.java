package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class ConfigCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendConfig(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isCommunityManager(player));
    }

}
