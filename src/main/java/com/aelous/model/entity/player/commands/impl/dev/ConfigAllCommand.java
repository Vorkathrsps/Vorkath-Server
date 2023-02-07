package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class ConfigAllCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (int i = Integer.parseInt(parts[1]); i < Integer.parseInt(parts[2]); i++) {
            player.getPacketSender().sendConfig(i, Integer.parseInt(parts[3]));
        }
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isDeveloper(player));
    }

}
