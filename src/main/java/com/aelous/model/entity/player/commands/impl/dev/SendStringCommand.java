package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class SendStringCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendString(Integer.parseInt(parts[1]), parts.length >= 3 ? parts[2] : ""+parts[1]);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isDeveloper(player);
    }
}
