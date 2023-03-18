package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;

public class ObjectCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        ObjectManager.addObj(new GameObject(Integer.parseInt(parts[1]),
            player.tile().copy(),
            parts.length < 3 ? 10 : Integer.parseInt(parts[2]),
            parts.length < 4 ? 0 : Integer.parseInt(parts[3])));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
