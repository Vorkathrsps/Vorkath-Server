package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class TourneyTeleportCommand implements Command{
    @Override
    public void execute(Player player, String command, String[] parts) {
        Tile tile = new Tile(3076, 3502);

        if(WildernessArea.isInWilderness(player) && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You can't use this command here.");
            return;
        }

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
        player.message("You have been teleported to the tournament area.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
