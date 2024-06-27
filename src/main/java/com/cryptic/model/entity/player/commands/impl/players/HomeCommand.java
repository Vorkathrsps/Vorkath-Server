package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameServer;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

public class HomeCommand implements Command {

    public void execute(Player player, String command, String[] parts) {
        Tile tile = GameServer.getServerType().getHomeTile();

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
        player.message("You have been teleported to home.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
