package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.object.GameObject;

public class ObjTypeCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int type = Integer.parseInt(parts[1]);
        var obj = new GameObject(Integer.parseInt(parts[1]), player.tile().copy(), type);
        player.getPacketSender().sendObject(obj);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
