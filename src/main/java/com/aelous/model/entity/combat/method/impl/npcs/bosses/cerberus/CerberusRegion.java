package com.aelous.model.entity.combat.method.impl.npcs.bosses.cerberus;

import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Jason MacKeigan on 2016-09-19 at 6:58 PM
 */
public enum CerberusRegion {

    WEST(4883, new Area(1228, 1243, 1252, 1257)),
    EAST(5395, new Area(1356, 1243, 1380, 1257)),
    NORTH(5140, new Area(1292, 1307, 1316, 1321));

    private final int id;
    private final Area area;

    CerberusRegion(int id, Area area) {
        this.id = id;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public Area getArea() {
        return area;
    }

    private Tile spawn = new Tile(getArea().x1() + 12, getArea().z1() + 7);

    private List<Tile> flames = Arrays.asList(
        new Tile(spawn.x - 1, spawn.level - 8),
        new Tile(spawn.x, spawn.level - 8),
        new Tile(spawn.x + 1, spawn.level - 8)
    );

    public static Optional<CerberusRegion> valueOfRegion(int id) {
        for(CerberusRegion cerberusRegion : CerberusRegion.values()) {
            if(cerberusRegion.getId() == id) {
                return Optional.of(cerberusRegion);
            }
        }
        return Optional.empty();
    }
}
