package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameServer;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

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
