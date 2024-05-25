package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;


public class NexCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.teleport(new Tile(2905, 5203, 0));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
