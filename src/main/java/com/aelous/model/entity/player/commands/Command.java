package com.aelous.model.entity.player.commands;

import com.aelous.model.entity.player.Player;

public interface Command {

    abstract void execute(Player player, String command, String[] parts);

    abstract boolean canUse(Player player);
}
