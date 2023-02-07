package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class FeaturesCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://aelous.net/features/");
        player.message("Opening the donator features in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
