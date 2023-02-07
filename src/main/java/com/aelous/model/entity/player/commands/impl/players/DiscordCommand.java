package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class DiscordCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://discord.com/invite/tJsWHM6FRr");
        player.message("Opening https://discord.com/invite/tJsWHM6FRr in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
