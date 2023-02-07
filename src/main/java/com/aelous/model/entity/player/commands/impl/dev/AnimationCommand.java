package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class AnimationCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int anim = Integer.parseInt(parts[1]);
        player.animate(new Animation(anim));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isDeveloper(player));
    }

}
