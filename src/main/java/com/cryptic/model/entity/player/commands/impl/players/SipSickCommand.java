package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | June, 21, 2021, 14:33
 * 
 */
public class SipSickCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/channel/UC74CQ_yflg-KQrjtCXpmozA");
        player.message("Opening Sipsick's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
