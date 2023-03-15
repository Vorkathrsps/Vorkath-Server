package com.aelous.model.content.instance;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Area;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class that manages all {@link InstancedArea} objects created.
 *
 * @author Jason MacKeigan
 * @date Jan 28, 2015, 1:07:55 PM
 */
public class InstancedAreaManager {

    /**
     * A single instance of this class for global usage
     */
    private static final InstancedAreaManager SINGLETON = new InstancedAreaManager();

    /**
     * The maximum height of any one instance
     */
    public static final int MAXIMUM_HEIGHT = 4 * 1024;

    /**
     * A mapping of all {@InstancedArea} objects that are being operated on
     * and are active.
     */
    private Map<Integer, InstancedArea> active = new HashMap<>();

    /**
     * A private empty {@link InstancedAreaManager} constructor exists to ensure that no other instance of this class can be created from outside this class.
     */
    private InstancedAreaManager() {
    }

    public InstancedArea ofZ(int z) {
        return active.get(z);
    }

    /**
     * Determines if the {@link InstancedArea} paramater exists within
     * the mapping of active {@link InstancedArea} objects and can be
     * disposed of.
     *
     * @param area    the instanced area
     * @return        true if the area exists in the mapping with the same height level
     *                 and the same reference
     */
    public boolean disposeOf(InstancedArea area) {
        area.dispose();
        return true;
    }

    /**
     * Creates a new {@link SingleInstancedArea} object with the given params
     * @param player    the player for this instanced area
     * @param area    the boundary of the area
     * @return    null if no height can be found for this area, otherwise the new
     * {@link SingleInstancedArea} object will be returned.
     */
    public InstancedArea createSingleInstancedArea(Player player, Area area) {
        SingleInstancedArea singleInstancedArea = new SingleInstancedArea(player, area);
        active.put(singleInstancedArea.getZLevel(), singleInstancedArea);
        return singleInstancedArea;
    }


    /**
     * Retrieves the single instance of this class
     * @return    the single instance
     */
    public static InstancedAreaManager getSingleton() {
        return SINGLETON;
    }

}
