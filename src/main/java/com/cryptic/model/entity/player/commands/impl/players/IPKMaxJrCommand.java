package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | June, 21, 2021, 14:33
 * 
 */
public class IPKMaxJrCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/channel/UCybjJ14mJjeeQaa7HqkyEzg");
        player.message("Opening I pk max jr's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
