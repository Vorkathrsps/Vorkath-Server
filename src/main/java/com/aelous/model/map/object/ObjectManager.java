package com.aelous.model.map.object;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

/**
 * A simple object manager used to manage
 * {@link GameObject}s which are spawned
 * by the server.
 * <p>
 * For client/map-objects, see {@link MapObjects}.
 *
 * @author Professor Oak
 */
public class ObjectManager {

    private static final Logger logger = LogManager.getLogger(ObjectManager.class);

    /**
     * Handles what happens when a player enters a new region.
     * We need to send all the objects related to that region.
     *
     * @param player The player whose changing region.
     */
    public static void onRegionChange(Player player) {
        for (GameObject object : World.getWorld().getSpawnedObjs()) {
            perform(object, OperationType.SPAWN);
        }
        for (GameObject obj : World.getWorld().getRemovedObjs()) {
            if (obj != null && Tile.sameH(player, obj)) {
                player.getPacketSender().sendObjectRemoval(obj);
            }
        }
    }

    /**
     * Registers a {@link GameObject} to the world.
     *
     * @param object       The object being registered.
     * @return
     */
    public static GameObject addObj(GameObject object) {
        World.getWorld().getSpawnedObjs().removeIf(o -> o.tile().equals(object.tile())
            && o.getType() == object.getType());

        World.getWorld().getSpawnedObjs().add(object);

        // Remove any previously spawned obj
        World.getWorld().getRemovedObjs().removeIf(o -> o.getType() == object.getType() && o.tile().equals(object.tile())); // TODO remove the clip of that obj

        perform(object, OperationType.SPAWN);
        return object;
    }

    /**
     * Deregisters a {@link GameObject} from the world.
     *
     * @param object       The object to deregister.
     */
    public static void removeObj(GameObject object) {
        World.getWorld().getSpawnedObjs().removeIf(o -> {
            return o.getId() == object.getId() &&
                o.getType() == object.getType() &&
                o.getRotation() == object.getRotation() &&
                o.tile().equals(object.tile);
        });
        perform(object, OperationType.DESPAWN);

        // Remove duplicates!
        World.getWorld().getRemovedObjs().removeIf(o -> o == null || o.tile() == null || o.getType() == object.getType() && o.tile().equals(object.tile()));
        World.getWorld().getRemovedObjs().add(object);
    }

    /**
     * Performs the given {@link OperationType} on the given {@link GameObject}.
     * Used for spawning and despawning objects.
     * If the object has an owner, it will only be spawned for them. Otherwise,
     * it will act as global.
     */
    private static void perform(GameObject object, OperationType type) {
        if (object.getId() == -1) {
            //logger.info("Gonna perform despawn object.");
            type = OperationType.DESPAWN;
        } else {
            //logger.info("Gonna perform spawn object " + object);
        }

        /**
         * We add/remove to/from mapobjects aswell. This is because the server handles clipping
         * via the map objects and also checks for cheatclients via them.
         */
        switch (type) {
            case SPAWN -> MapObjects.add(object);
            case DESPAWN -> MapObjects.remove(object);
        }

        //Spawn/despawn the actual {@link GameObject}
        if (object.getSpawnedfor().isPresent()) {
            switch (type) {
                case SPAWN:
                case DESPAWN:
                    if (type == OperationType.SPAWN) {
                        object.getSpawnedfor().get().getPacketSender().sendObject(object);
                    } else {
                        object.getSpawnedfor().get().getPacketSender().sendObjectRemoval(object);
                    }
                    break;
            }
        } else {
            switch (type) {
                case SPAWN:
                case DESPAWN:
                    for (Player player : World.getWorld().getPlayers()) {
                        if (player == null)
                            continue;
                        if (player.tile().isWithinDistance(object.tile(), 64)) {
                            if (type == OperationType.SPAWN) {
                                player.getPacketSender().sendObject(object);
                            } else {
                                player.getPacketSender().sendObjectRemoval(object);
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Checks if a {@link GameObject} exists at the given location.
     */
    public static boolean exists(Tile tile) {
        for (GameObject object : World.getWorld().getSpawnedObjs()) {
            System.out.println("object: " + object.getId() + " tile: " + object.tile().getX() + " " + object.tile().getY() + " " + object.tile().getZ());
            if (object.tile().equals(tile)) {
                System.out.println("Exists!");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given id.
     */
    public static boolean exists(int id, Tile tile) {
        for (GameObject object : World.getWorld().getSpawnedObjs()) {
            if (object.tile().equals(tile)) {
                if (object.getId() == id) {
                    return true;
                }
            }
        }
        return MapObjects.exists(id, tile);
    }

    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given type.
     */
    public static boolean objWithTypeExists(int type, Tile tile) {
        for (GameObject object : World.getWorld().getSpawnedObjs()) {
            if (object.tile().equals(tile)) {
                //System.out.printf("found %s%n", object);
                if (object.getType() == type) {
                    return true;
                }
            }
        }
        return MapObjects.around(tile, 0)
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(o -> o.getType() == type);
    }

    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given id.
     */
    public static GameObject objById(int id, Tile tile) {
        Optional<GameObject> obj = World.getWorld().getSpawnedObjs().stream().filter(o -> o.getId() == id && o.tile().equals(tile)).findAny();
        return obj.orElse(MapObjects.get(id, tile).orElse(null));
    }

    public static GameObject get(int obj) {
        for (GameObject object : World.getWorld().getSpawnedObjs()) {
            if (object.getId() == obj) {
                return object;
            }
        }
        return null;
    }

    public static void openAndCloseDoor(GameObject opendoor, GameObject closedoor) {
        TaskManager.submit(new Task("open_door_task", 2) {

            @Override
            protected void execute() {
                //System.out.println("Opening door...");
                addObj(opendoor);
                stop();
            }
        });

        TaskManager.submit(new Task("close_door_task", 3) {

            @Override
            protected void execute() {
                //System.out.println("closing door...");
                addObj(closedoor);
                stop();
            }
        });
    }

    public static void replaceWith(GameObject obj, GameObject newObj) {
        removeObj(obj);
        addObj(newObj);
    }

    /**
     * Replaces an {@link GameObject} at its current location, with a new
     * {@link GameObject}.
     *
     * @param original    The original object that we're going to replace
     * @param replacement The replacement object (new object)
     * @param cycles      The amount of cycles before we change the replacement with
     *                    original
     */
    public static void replace(final GameObject original, final GameObject replacement, int cycles) {
        removeObj(original);
        if (replacement != null) {
            addObj(replacement);
        }
        if (cycles < 0)
            return;
        TaskManager.submit(new Task("ObjectReplaceTask", cycles) {
            @Override
            public void execute() {
                GameObject addOrig = new GameObject(original.getId(), original.tile(), original.getType(), original.getRotation());
                addObj(addOrig);
                stop();
            }
        });

        //System.out.println("Replacing: "+original.toString());
    }

    /**
     * The possible operation types.
     */
    public enum OperationType {
        SPAWN, DESPAWN
    }

    public static void spawnTempObject(final GameObject ob, final int cycles) {
        addObj(ob);
        TaskManager.submit(new Task("ObjectManager:spawnTempObject", cycles) {
            @Override
            public void execute() {
                removeObj(ob);
                this.stop();
            }

            @Override
            public void stop() {
                setEventRunning(false);
            }
        });
    }
}
