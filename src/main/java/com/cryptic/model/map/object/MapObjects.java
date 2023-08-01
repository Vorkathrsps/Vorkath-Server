package com.cryptic.model.map.object;

import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

/**
 * uses prof oak's old class name but code is runite
 * @author Jak|shadowrs
 * @author Runite team
 */
@SuppressWarnings("ALL")
public class MapObjects {

    /**
     * Attempts to get an object with the given id and position.
     *
     * @param id
     * @param tile
     */
    public static Optional<GameObject> get(int id, Tile tile) {
        return get(obj -> id == obj.getId() || (id == -1 && obj.getType() == 10), tile);
    }

    public static Optional<GameObject> get(Predicate<GameObject> predicate, Tile tile) {

        // don't create, no need to make it.
        var t = Tile.get(tile.x, tile.y, tile.level);
        if (t == null)
            return Optional.empty();

        //Go through the objects in the list..
        ArrayList<GameObject> list = t.gameObjects;
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
    public static @Nonnull List<GameObject> getAll(Tile tile) {
        return getAll(tile, 0);
    }

    public static @Nonnull List<GameObject> getAll(Tile tile, final int radius) {
        if (radius == 0) {
            RegionManager.loadMapFiles(tile.x, tile.y);
            var t = Tile.get(tile);
            if (t == null)
                return List.of();
            var o = t.gameObjects;
            return o == null || o.size() == 0 ? List.of() : o;
        }
        ArrayList<GameObject> list = null;
        for (int x = tile.getX() - radius; x < tile.getX() + radius; x++) {
            for (int y = tile.getY() - radius; y < tile.getY() + radius; y++) {
                RegionManager.loadMapFiles(x, y);
                var t = Tile.get(x, y, tile.level);
                if (t == null) continue;
                var o = t.gameObjects;
                if (o == null || o.size() == 0) continue;
                if (list == null)
                    list = new ArrayList<>();
                list.addAll(o);
                //System.out.println("found "+ Arrays.toString(o.stream().map(e -> e.definition().name).toArray()));
            }
        }
        //System.out.println("found " + list.size() + " at " + tile);
        return list == null || list.size() == 0 ? List.of() : list;
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

}
