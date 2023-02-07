package com.aelous.model.map.object;

import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Map objects are objects that are in the maps.
 * These are loaded when the maps are so that we can
 * verify that an object exists when a player
 * tries to interact with it.
 *
 * @author Professor Oak
 */
public class MapObjects {

    /**
     * A map which holds all of our map objects. This contains objects from the cache, the
     * landscape from files before any objects are custom spawned or adjusted
     */
    public static final Map<Long, ArrayList<GameObject>> mapObjects = new HashMap<Long, ArrayList<GameObject>>();

    /**
     * Attempts to get an object with the given id and position.
     *
     * @param id
     * @param tile
     */
    public static Optional<GameObject> get(int id, Tile tile) {
        return get(obj -> id == obj.getId(), tile);
    }

    public static Optional<GameObject> get(Predicate<GameObject> predicate, Tile tile) {
        //Load region..
        RegionManager.loadMapFiles(tile.getX(), tile.getY());

        //Get hash..
        long hash = getHash(tile.getX(), tile.getY(), tile.getLevel());

        //Check if the map contains the hash..
        if (!mapObjects.containsKey(hash)) {
            return Optional.empty();
        }

        //Go through the objects in the list..
        ArrayList<GameObject> list = mapObjects.get(hash);
        if (list != null) {
            Iterator<GameObject> it = list.iterator();
            for (; it.hasNext(); ) {
                GameObject o = it.next();
                if (o != null && predicate.test(o)
                    && o.tile().equals(tile)) {
                    return Optional.of(o);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if an object with the given id and position exists.
     *
     * @param id
     * @param tile
     * @return
     */
    public static boolean exists(int id, Tile tile) {
        return get(id, tile).isPresent();
    }

    /**
     * Checks if an gameobject exists.
     *
     * @param object
     * @return
     */
    public static boolean exists(GameObject object) {
        return get(object.getId(), object.tile()).isPresent();
    }

    /**
     * Attempts to add a new object to our map of mapobjects.
     *
     * @param object
     */
    public static void add(GameObject object) {
        RegionManager.loadMapFiles(object.getX(), object.getY());
        //Get hash for object..
        long hash = getHash(object.tile().getX(), object.tile().getY(), object.tile().getLevel());

        if (mapObjects.containsKey(hash)) {
            //Check if object already exists in this list..
            boolean exists = false;
            List<GameObject> list = mapObjects.get(hash);
            Iterator<GameObject> it = list.iterator();
            for (; it.hasNext(); ) {
                GameObject o = it.next();
                if (o.equals(object)) {
                    exists = true;
                    break;
                }
            }
            //If it didn't exist, add it.
            if (!exists) {
                mapObjects.get(hash).add(object);
            }
        } else {
            ArrayList<GameObject> list = new ArrayList<GameObject>();
            list.add(object);
            mapObjects.put(hash, list);
        }

        //Add clipping for object.
        object.clip(false);
    }

    public static List<GameObject> at(Tile tile) {
        return around(tile, 0);
    }

    public static List<GameObject> around(Tile tile, int radius) {
        if (radius == 0) {
            RegionManager.loadMapFiles(tile.x, tile.y);
            long hash = getHash(tile.x, tile.y, tile.level);
            if (mapObjects.containsKey(hash)) {
                return mapObjects.get(getHash(tile.x, tile.y, tile.level));
            }
        }
        ArrayList<GameObject> list = new ArrayList<>();
        for (int x = tile.getX() - radius; x < tile.getX() + radius; x++) {
            for (int y = tile.getY() - radius; y < tile.getY() + radius; y++) {
                RegionManager.loadMapFiles(x, y);
                long hash = getHash(x, y, tile.level);
                if (mapObjects.containsKey(hash)) {
                    list.addAll(mapObjects.get(getHash(x, y, tile.level)));
                }
            }
        }
        //System.out.println("found " + list.size() + " at " + tile);
        return list;
    }

    /**
     * Attempts to remove the given object from our map of mapobjects.
     *
     * @param object
     */
    public static void remove(GameObject object) {
        //Get hash for object..
        long hash = getHash(object.tile().getX(), object.tile().getY(), object.tile().getLevel());

        //Attempt to delete..
        if (mapObjects.containsKey(hash)) {
            Iterator<GameObject> it = mapObjects.get(hash).iterator();
            while (it.hasNext()) {
                GameObject o = it.next();
                if (o.getId() == object.getId() && o.tile().equals(object.tile())) {
                    it.remove();
                }
            }
        }

        //Remove clipping from this area..
        object.clip(true);
    }

    /**
     * Removes all objects in this position.
     *
     * @param tile
     */
    public static void clear(Tile tile, int clipShift) {
        //Get hash for pos..
        long hash = getHash(tile.getX(), tile.getY(), tile.getLevel());

        //Attempt to delete..
        if (mapObjects.containsKey(hash)) {
            Iterator<GameObject> it = mapObjects.get(hash).iterator();
            while (it.hasNext()) {
                GameObject o = it.next();
                if (o.tile().equals(tile)) {
                    it.remove();
                }
            }
        }

        //Remove clipping from this area..
        RegionManager.removeClipping(tile.getX(), tile.getY(), tile.getLevel(), clipShift);
    }

    /**
     * Gets the hash for a map object.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static long getHash(int x, int y, int z) {
        return (z + ((long) x << 24) + ((long) y << 48));
    }
}
