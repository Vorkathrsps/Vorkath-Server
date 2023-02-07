package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.GameServer;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

/**
 * @author Patrick van Elderen | January, 25, 2021, 21:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DuelArenaCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (!GameServer.properties().enableDueling && player.getPlayerRights().isDeveloper(player)) {
            return;
        }

        Tile tile = GameServer.properties().duelTile;

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
