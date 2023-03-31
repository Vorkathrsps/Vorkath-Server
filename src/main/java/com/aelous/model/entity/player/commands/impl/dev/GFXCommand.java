package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class GFXCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int gfx = Integer.parseInt(parts[1]);
        player.performGraphic(new Graphic(gfx, GraphicHeight.HIGH, 0));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isDeveloper(player));
    }

}
