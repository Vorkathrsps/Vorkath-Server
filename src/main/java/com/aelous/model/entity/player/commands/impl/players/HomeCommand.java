package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.GameServer;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

public class HomeCommand implements Command {

    public void execute(Player player, String command, String[] parts) {
        Tile tile = GameServer.properties().defaultTile;

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
