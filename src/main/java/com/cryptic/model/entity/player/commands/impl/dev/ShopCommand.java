package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class ShopCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(parts.length != 2)
            return;
        int shop= Integer.parseInt(parts[1]);
        World.getWorld().shop(shop).open(player);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
