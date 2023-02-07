package com.aelous.model.content.instance;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;

public class SingleInstancedArea extends InstancedArea {

    /**
     * The player in this single instanced area
     */
    protected Player player;

    /**
     * Creates a new single instanced area for a player
     * @param area    the boundary of the instanced area
     * @param height    the height of the instanced area
     */
    public SingleInstancedArea(Area area, int height) {
        super(area, height);
    }

    /**
     * Creates a new single instanced area for a player
     * @param player    the player in the instanced area
     * @param area    the boundary of the instanced area
     * @param height    the height of the instanced area
     */
    public SingleInstancedArea(Player player, Area area, int height) {
        super(area, height);
        this.player = player;
    }

    /**
     * The player for this instanced area
     * @return    the player
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public void onDispose() {

    }
}
