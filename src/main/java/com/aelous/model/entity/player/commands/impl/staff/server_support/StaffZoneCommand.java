package com.aelous.model.entity.player.commands.impl.staff.server_support;

import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class StaffZoneCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (WildernessArea.inWild(player)&& !player.getPlayerRights().isDeveloper(player)) {
            player.message("You can't use this command in the wilderness.");
            return;
        }
        if (Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            Teleports.basicTeleport(player, new Tile(3032, 6121));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isStaffMember(player);
    }
}
