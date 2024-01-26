package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class GFXCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int startGfx = Integer.parseInt(parts[1]);

        player.graphic(startGfx);
    }


    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
