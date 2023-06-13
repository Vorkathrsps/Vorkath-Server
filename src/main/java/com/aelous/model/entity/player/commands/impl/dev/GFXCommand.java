package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

public class GFXCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int startGfx = Integer.parseInt(parts[1]);

        player.graphic(startGfx);
    }


    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
