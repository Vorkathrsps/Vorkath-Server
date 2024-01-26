package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class DzoneCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (WildernessArea.isInWilderness(player)&& !player.getPlayerRights().isCommunityManager(player)) {
            player.message("You can't use this command in the wilderness.");
            return;
        }
        if (Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
            Teleports.basicTeleport(player, new Tile(2457, 2858));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getMemberRights().isRegularMemberOrGreater(player) || player.getPlayerRights().isStaffMember(player);
    }
}
