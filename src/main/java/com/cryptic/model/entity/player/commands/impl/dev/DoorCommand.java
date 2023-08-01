package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.object.doors.Door;
import com.cryptic.model.map.object.doors.Doors;
import com.cryptic.model.map.position.Tile;

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
