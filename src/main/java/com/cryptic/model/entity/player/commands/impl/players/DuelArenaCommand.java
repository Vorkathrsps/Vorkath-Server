package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameServer;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | January, 25, 2021, 21:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DuelArenaCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (!GameServer.properties().enableDueling && player.getPlayerRights().isCommunityManager(player)) {
            return;
        }

        Tile tile = GameServer.properties().duelTile.tile();

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
