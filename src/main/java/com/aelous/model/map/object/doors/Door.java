package com.aelous.model.map.object.doors;

import com.aelous.cache.definitions.ObjectDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;
import java.util.function.Function;

public class Door {

    private int id;
    private int toId;
    private boolean closed;
    private boolean open;
    private Function<Door, GameObject> fn;

    public Door(int id, int toId, boolean closed, boolean open) {
        this.id = id;
        this.toId = toId;
        this.closed = closed;
        this.open = open;
    }

    public GameObject open(GameObject doorObj, Player player, boolean inverse) {
        if (doorObj.getId() == 9398 && doorObj.tile().equals(3098, 3107)) {
            player.message("You need to talk to the Guide to proceed.");
            return doorObj;
        }
        int orientation = doorObj.getRotation();
        ObjectDefinition def = World.getWorld().definitions().get(ObjectDefinition.class, doorObj.getId());
        if (def == null) {
            System.err.println("Unrecognized definition for door "+ doorObj.getId());
            return doorObj;
        }

        boolean flipped = def.vflip;
        if (flipped) {
            orientation -= 1;
        } else {
            orientation += 1;
        }

        orientation &= 3;
        var target = inverse ? inverseOffset(doorObj.tile(), orientation) : offset(doorObj.tile(), orientation);

        var replacement = new GameObject(toId, target, doorObj.getType(), orientation);
        // Mechanic for gates getting stuck.
        var openCycleIds = doorObj.<ArrayList<Integer>>getAttribOr(AttributeKey.DOOR_USES, new ArrayList<>());
        openCycleIds.add(World.getWorld().cycleCount());
        replacement.putAttrib(AttributeKey.DOOR_USES, openCycleIds);
        doorObj.replaceWith(replacement, true);
        return doorObj;
    }

    public GameObject close(GameObject doorObj, Player player, boolean targetCal, boolean flip) {
        var openCycleIds = doorObj.<ArrayList<Integer>>getAttribOr(AttributeKey.DOOR_USES, new ArrayList<Integer>());
        doorObj.putAttrib(AttributeKey.DOOR_USES, openCycleIds);

        var cur = World.getWorld().cycleCount();
        openCycleIds.removeIf(p -> p < (cur - 50));// Remove old "open times" older than 30 seconds ago

        if (openCycleIds.size() >= 10) {
            // Stuck message.
            return doorObj;
        }
        openCycleIds.add(cur);

        int orientation = doorObj.getRotation();
        ObjectDefinition def = World.getWorld().definitions().get(ObjectDefinition.class, doorObj.getId());
        if (def == null) {
            System.err.println("Unrecognized definition for door "+ doorObj.getId());
            return doorObj;
        }

        int face = orientation;
        if (flip) {
            boolean flipped = def.vflip;
            if (flipped) {
                face += 1;
            } else {
                face -= 1;
            }
        }
        face &= 3;
        // ok well i suppose we can just add removed objects to a new map and send delete on that packet
        // i wanna check how oss does it wanna open it?
        var target = targetCal ? offset(doorObj.tile(), orientation) : inverseOffset(doorObj.tile(), orientation);
        var replacement = new GameObject(toId, target, doorObj.getType(), face);
        doorObj.replaceWith(replacement, true);
        return replacement;
    }

    private Tile offset(Tile tile, int dir) {
        return switch (dir) {
            case 0 -> tile.transform(0, 1);
            case 1 -> tile.transform(1, 0);
            case 2 -> tile.transform(0, -1);
            case 3 -> tile.transform(-1, 0);
            default -> tile;
        };
    }

    private Tile inverseOffset(Tile tile, int dir) {
        return switch (dir) {
            case 0 -> tile.transform(0, -1);
            case 1 -> tile.transform(-1, 0);
            case 2 -> tile.transform(0, 1);
            case 3 -> tile.transform(1, 0);
            default -> tile;
        };
    }

    public int id() {
        return id;
    }

    public int toId() {
        return toId;
    }

    public void setClosed(boolean val) {
        this.closed = val;
    }

    public boolean closed() {
        return closed;
    }

    public void setOpen(boolean val) {
        this.open = val;
    }

    public boolean open() {
        return open;
    }
}
