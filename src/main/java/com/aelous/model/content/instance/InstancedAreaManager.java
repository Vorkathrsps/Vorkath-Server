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

    /**
     * Creates a new {@link SingleInstancedArea} object with the given params
     * @param zLevel
     *             the zLevel of the new instance
     * @param instance
     *             the instance that will be added
     * @return
     *             null if no zLevel can be found for this area, otherwise the new
     *             {@link SingleInstancedArea} object will be returned.
     */
    public void add(int zLevel, InstancedArea instance) {
        active.put(zLevel, instance);
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
    public boolean disposeOf(InstancedArea area) {//doubt this still works since we not longer use those height maps
        if (area == null)
            return false;
        int z = area.getzLevel();
        if (!active.containsKey(z)) {
            return false;
        }
        InstancedArea found = active.get(z); // this system? should work
        if (!found.equals(area)) {
            return false;
        }

        for (GroundItem gi : GroundItemHandler.getGroundItems()) {
            if (!gi.getTile().inArea(found.area))
                continue;

            GroundItemHandler.sendRemoveGroundItem(gi);
        }

        active.remove(z);
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
        int z = anyZ();//getNextOpenZLevel(area);
        if (z == -1) {
            return null;
        }
        SingleInstancedArea singleInstancedArea = new SingleInstancedArea(player, area, z);
        active.put(z, singleInstancedArea);
        return singleInstancedArea;
    }

    public InstancedArea createMultiInstancedArea(Area area) {
        int z = getNextOpenZLevel(area);
        if (z == -1) {
            return null;
        }
        MultiInstancedArea mia = new MultiInstancedArea(area, z);
        active.put(z, mia);
        return mia;
    }

    /**
     * Retrieves an open height level by sifting through the mapping and attempting to retrieve the lowest height level.
     *
     * @return the next lowest, open height level will be returned otherwise -1 will be returned. When -1 is returned it signifies that there are no heights open from 0 to
     *         {@link #MAXIMUM_HEIGHT}.
     */
    public int getNextOpenZLevel(Area area) {
        for (int z = 4; z < MAXIMUM_HEIGHT; z += 4) {
            if (active.containsKey(z)) {
                continue;
            }
            final int zLvl = z;
            if (World.getWorld().getPlayers().stream().anyMatch(p -> Objects.nonNull(p) && p.tile().inArea(area) && p.tile().level == zLvl)) {
                continue;
            }
            return z;
        }
        return -1;
    }

    public int anyZ() {
        int attempts = 0;
        while (attempts < 100) {
            int z = World.getWorld().random(1024)*4;
            if (!active.containsKey(z)) {
                return z;
            }
            attempts++;
        }
        return -1;
    }

    /**
     * Retrieves an open height level by sifting through the mapping and attempting to retrieve the lowest height level.
     *
     * @return the next lowest, open height level will be returned otherwise -1 will be returned. When -1 is returned it signifies that there are no heights open from 0 to
     *         {@link MAXIMUM_HEIGHT}.
     */
    public int getNextOpenHeightCust(Area area, int level) {
        for (int z = level; z < MAXIMUM_HEIGHT; z += 4) {
            if (active.containsKey(z)) {
                continue;
            }
            final int z1 = z;
            if (World.getWorld().getPlayers().stream().anyMatch(p -> Objects.nonNull(p) &&  p.tile().inArea(area) && p.tile().level == z1)) {
                continue;
            }
            return z;
        }
        return -1;
    }

    /**
     * Retrieves the single instance of this class
     * @return    the single instance
     */
    public static InstancedAreaManager getSingleton() {
        return SINGLETON;
    }

}
