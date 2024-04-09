package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class VoteCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://valorps.everythingrs.com/vote/");
        player.message("Opening https://valorps.everythingrs.com/vote/ in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
