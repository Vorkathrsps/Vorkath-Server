package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.World;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

public class GFXCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int gfx = Integer.parseInt(parts[1]);
        //player.graphic(gfx, GraphicHeight.HIGH, 0);
        World.getWorld().tileGraphic(gfx, new Tile(player.getX(), player.getY(), player.getZ()), 0, 0);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
