package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | January, 11, 2021, 18:06
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 * @Chase - very sexy boi
 */
public class ZulrahCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isRegularMemberOrGreater(player)) {
            player.message("You have to be at least a Member to use this command.");
            return;
        }

        Tile tile = new Tile(2201, 3057, 0);

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
        player.message("You have been teleported to Zulrah!");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
