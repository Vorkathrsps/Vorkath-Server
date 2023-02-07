package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.content.areas.lumbridge.dialogue.Hans;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class PlayTimeCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.message(Hans.getTimeDHS(player));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
