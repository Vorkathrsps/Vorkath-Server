package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.object.doors.Door;
import com.aelous.model.map.object.doors.Doors;
import com.aelous.model.map.position.Tile;

public class DoorCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int id = Integer.parseInt(parts[1]);
        int relativeId = Integer.parseInt(parts[2]);
        int rot = Integer.parseInt(parts[3]);
        Tile tile = player.tile();
        GameObject spawned = new GameObject(id, player.tile(), 0, rot);
        ObjectManager.addObj(spawned);
        Doors.CACHE.add(new Door(id, relativeId, true, false));
        player.message("Spawned door "+ id +" at "+ tile.toString() +" with rotation " + rot);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }
}
