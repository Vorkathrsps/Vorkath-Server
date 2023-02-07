package com.aelous.model.content.instance;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 25, 2020
 */
public class MultiInstancedArea extends InstancedArea {

    /**
     * Creates a new single instanced area for multiple players
     *
     * @param area the boundary of the instanced area
     * @param zLvl the zLvl of the instanced area
     */
    public MultiInstancedArea(Area area, int zLvl) {
        super(area, zLvl);
    }

    public List<Player> getPlayers() {
        return World.getWorld().getPlayers().stream().filter(player -> player.tile().inArea(super.area) && player.tile().level == zLevel).collect(Collectors.toList());
    }

    @Override
    public void onDispose() {

    }
}
