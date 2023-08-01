package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class AlertCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
       String msg = "";
       for (int i = 1; i < parts.length; i++) {
           msg += parts[i] + " ";
       }
        World.getWorld().sendWorldMessage("Alert##Notification##" + msg+ "##From: " + player.getUsername() );
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
