package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.model.content.areas.lumbridge.dialogue.Hans;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

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
