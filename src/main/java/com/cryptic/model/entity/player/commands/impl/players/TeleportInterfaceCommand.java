package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class TeleportInterfaceCommand  implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setCurrentTabIndex(1);
        player.getInterfaceManager().open(88000);
        player.getnewteleInterface().drawInterface(88005);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
