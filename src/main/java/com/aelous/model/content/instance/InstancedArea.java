package com.aelous.model.content.instance;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

public abstract class InstancedArea {

    /**
     * The boundary or location for this instanced area
     */
    protected Area area;

    /**
     * The height of this area
     */
    protected int zLevel;

    /**
     * Creates a new area with a boundary
     * @param area    the boundary or location
     * @param zLevel    the height of the area
     */
    public InstancedArea(Area area, int zLevel) {
        this.area = area;
        this.zLevel = zLevel;
    }

    /**
     * When an instanced area is disposed it should be common to clean up
     * that instanced area so that it can be re-used by others.
     * <p>
     * This function is called when the {@link InstancedAreaManager#disposeOf(InstancedArea)} function
     * is referenced.
     * </p>
     */
    public abstract void onDispose();

    /**
     * Determines the height of this area
     * @return    the height
     */
    public int getzLevel() {
        return zLevel;
    }

    /**
     * The boundary or location of this instanced area
     * @return    the boundary
     */
    public Area getArea() {
        return area;
    }

    private OnTele onTeleport;

    public void setOnTeleport(OnTele c) {
        this.onTeleport = c;
    }

    public void onTeleport(Player player, Tile tile) {
         if (onTeleport != null)
             onTeleport.accept(player, tile);
    }
}
