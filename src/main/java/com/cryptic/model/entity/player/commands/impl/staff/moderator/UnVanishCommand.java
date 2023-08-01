package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class UnVanishCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.looks().hide(false);
        player.message("You are now exposed again.");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isModerator(player));
    }
}
