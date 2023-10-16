package com.cryptic.model.map.object;

import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.Region;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * GameObject tracking.
 * @author Shadowrs + Runite team
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
        for (Region surroundingRegion : player.getSurroundingRegions()) {
            for (Tile activeTile : surroundingRegion.activeTiles) {
                if (activeTile.getZ() == player.getZ())
                    activeTile.update(player);
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
        object.spawn();
        return object;
    }

    /**
     * Deregisters a {@link GameObject} from the world.
     *
     * @param object       The object to deregister.
     */
    public static void removeObj(GameObject object) {
        object.remove();
    }

    /**
     * Checks if a {@link GameObject} exists at the given location.
     */
    public static boolean exists(Tile tile) {
        return MapObjects.get(o -> o.getType() == 10, tile).isPresent();
    }

    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given id.
     */
    public static boolean exists(int id, Tile tile) {
        return MapObjects.get(o -> o.getId() == id || id == -1, tile).isPresent();
    }

    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given type.
     */
    public static boolean objWithTypeExists(int type, Tile tile) {
        return MapObjects.getAll(tile, 0)
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(o -> o.getType() == type);
    }



    /**
     * Checks if a {@link GameObject} exists at the given location
     * with the given id.
     */
    public static GameObject objById(int id, Tile tile) {
        return MapObjects.get(id, tile).orElse(null);
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
        obj.setId(newObj.getId());
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
        original.setId(replacement != null ? replacement.getId() : -1);
        if (cycles < 0)
            return;
        Chain.bound("lever_replacement_task").runFn(cycles, () -> original.setId(original.originalId));

        System.out.println("Replacing: " + original);
    }

}
