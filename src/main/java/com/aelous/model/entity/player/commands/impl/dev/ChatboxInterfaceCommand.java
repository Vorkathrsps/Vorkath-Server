package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class ChatboxInterfaceCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendChatboxInterface(Integer.parseInt(parts[1]));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isAdministrator(player));
    }

}
