package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class POScommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.message(player.tile().toString()+" region: "+player.tile().region()+". wild: "+ WildernessArea.inWild(player)+". Chunk: " +player.tile().chunk()+" local "+(player.getX() & 63)+" "+(player.getAbsY() & 63));
    }

    @Override
    public boolean canUse(Player player) {

        return (player.getPlayerRights().isDeveloper(player));
    }

}
