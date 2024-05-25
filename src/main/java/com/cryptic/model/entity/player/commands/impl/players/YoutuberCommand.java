package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class YoutuberCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {

    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
