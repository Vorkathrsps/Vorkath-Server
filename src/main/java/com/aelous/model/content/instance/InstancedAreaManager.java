package com.aelous.model.content.instance;

import com.aelous.model.map.position.Area;

import java.util.HashMap;
import java.util.Map;

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

    public InstancedArea createInstancedArea(Area area) {
        InstancedArea singleInstancedArea = new InstancedArea(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, area);
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
