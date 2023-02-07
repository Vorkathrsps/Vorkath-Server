package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class BMMultiplierCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int amt = Math.max(1, Math.min(5, Integer.parseInt(parts[1])));
        player.message("BM multiplier changed from " + World.getWorld().bmMultiplier + " to " + amt + ".");
        World.getWorld().bmMultiplier = amt;
        World.getWorld().sendWorldMessage("<col=ca0d0d> Double BM is now " + (amt == 1 ? "OFF" : "ON") + ".");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
