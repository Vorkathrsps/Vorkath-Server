package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | January, 11, 2021, 18:09
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MageBankCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        Tile tile = new Tile(2539, 4716);

        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC) || !Teleports.pkTeleportOk(player, tile)) {
            return;
        }

        Teleports.basicTeleport(player, tile);
        player.message("You have been teleported to the mage bank.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
