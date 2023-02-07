package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.GameServer;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class StoreCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://aelous.net/store/");
        player.message("Opening https://aelous.net/store/ in your web browser...");
        if(GameServer.properties().promoEnabled) {
            player.getPaymentPromo().open();
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
