package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.object.GameObject;

public class ObjTypeCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int type = Integer.parseInt(parts[1]);
        player.getPacketSender().sendObject(new GameObject(Integer.parseInt(parts[1]), player.tile().copy(), type));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
