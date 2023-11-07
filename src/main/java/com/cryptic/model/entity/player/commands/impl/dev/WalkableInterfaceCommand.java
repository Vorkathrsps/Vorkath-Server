package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | Zerikoth | PVE
 * @date maart 21, 2020 22:38
 */
public class WalkableInterfaceCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getInterfaceManager().sendOverlay(Integer.parseInt(parts[1]));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isDeveloper(player));
    }

}
