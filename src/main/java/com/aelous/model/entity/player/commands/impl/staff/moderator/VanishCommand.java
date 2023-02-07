package com.aelous.model.entity.player.commands.impl.staff.moderator;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class VanishCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.looks().hide(true);
        player.message("You are now hidden.");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isModerator(player));
    }
}
