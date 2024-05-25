package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;
/**
 * @author Malefique
 * @Since december 10, 2020
 */
public class ShopsCommand implements Command {

    public void execute(Player player, String command, String[] parts) {
        Tile tile = new Tile(3080, 3511, 0);

        if (Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            Teleports.basicTeleport(player, tile);
        }

        player.message("You have been teleported to the shops area.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
