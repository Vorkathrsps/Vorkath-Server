package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | June, 11, 2021, 10:23
 * 
 */
public class RaidsTeleportCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        Tile tile = new Tile(1245, 3561);

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
        player.message("You have been teleported to the raiding area.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
