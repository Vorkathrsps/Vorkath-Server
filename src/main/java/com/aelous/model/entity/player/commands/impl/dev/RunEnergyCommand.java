package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class RunEnergyCommand implements Command {
      @Override
    public void execute(Player player, String command, String[] parts) {
          // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (parts.length < 2) {
            player.message("Run energy usage: ::energy 0-100");
            return;
        }
        double runEnergy = Double.parseDouble(parts[1]);
        if (runEnergy < 0 || runEnergy > 100) {
            player.message("Please enter a run energy between 0 and 100.");
            return;
        }
        player.setRunningEnergy(runEnergy, true);
        player.message("You have changed your run energy to: " + runEnergy);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
