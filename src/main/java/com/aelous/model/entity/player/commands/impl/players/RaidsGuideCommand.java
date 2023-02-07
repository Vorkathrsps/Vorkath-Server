package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class RaidsGuideCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://youtu.be/bw_e9lo6qVo");
        player.message("Opening the raids guide in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
