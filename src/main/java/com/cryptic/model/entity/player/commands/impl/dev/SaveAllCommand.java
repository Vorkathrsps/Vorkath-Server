package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class SaveAllCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        //Proper way of saving, use the logout service.
        World.getWorld().ls.saveAllAsync();
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
