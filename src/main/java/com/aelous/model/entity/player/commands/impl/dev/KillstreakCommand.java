package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class KillstreakCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.putAttrib(AttributeKey.KILLSTREAK, Integer.valueOf(parts[1]));
        player.message("Current killstreak set to "+parts[1]);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
