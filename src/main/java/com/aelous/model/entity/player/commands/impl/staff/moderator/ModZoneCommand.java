package com.aelous.model.entity.player.commands.impl.staff.moderator;

import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

public class ModZoneCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.animate(new Animation(714, Priority.HIGH));
        player.performGraphic(new Graphic(308, GraphicHeight.MIDDLE));
        Chain.bound(player).name("ModZoneTeleportTask").runFn(2, () -> {
            player.teleport(new Tile(2525, 4776));
            player.animate(new Animation(715, Priority.HIGH));
        });

        player.message("Welcome to the mod zone!");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isModerator(player));
    }

}
