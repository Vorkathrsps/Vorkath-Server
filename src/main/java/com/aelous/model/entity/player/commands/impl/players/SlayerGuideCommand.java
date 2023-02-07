package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class SlayerGuideCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://aelous.net/slayer-guide/");
        player.message("Opening the slayer guide in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
