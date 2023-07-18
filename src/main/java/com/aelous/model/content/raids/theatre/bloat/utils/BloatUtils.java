package com.aelous.model.content.raids.theatre.bloat.utils;

import com.aelous.model.entity.MovementQueue;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;

import java.util.Random;

import static com.aelous.model.content.raids.theatre.bloat.handler.BloatProcess.BLOAT_AREA;
import static com.aelous.model.content.raids.theatre.bloat.handler.BloatProcess.LIMB_GRAPHICS;

public class BloatUtils {

    private int getMinX(Area area) {
        return Math.min(area.x1(), area.x2());
    }

    private int getMinY(Area area) {
        return Math.min(area.y1, area.y2);
    }

    private int getMaxX(Area area) {
        return Math.max(area.x1, area.x2);
    }

    private int getMaxY(Area area) {
        return Math.max(area.y1, area.y2);
    }

    public Tile getRandomTile() {
        Random random = new Random();
        int minX = getMinX(BLOAT_AREA);
        int minY = getMinY(BLOAT_AREA);
        int maxX = getMaxX(BLOAT_AREA);
        int maxY = getMaxY(BLOAT_AREA);

        int randomX = random.nextInt(maxX - minX + 1) + minX;
        int randomY = random.nextInt(maxY - minY + 1) + minY;

        return new Tile(randomX, randomY);
    }
    public boolean isTileValid(Tile tile, Tile randomTile) {
        return MovementQueue.dumbReachable(tile.getX(), tile.getY(), randomTile) && !RegionManager.blocked(randomTile);
    }

    public int getRandomLimbGraphic() {
        Random random = new Random();
        int randomIndex = random.nextInt(LIMB_GRAPHICS.length);
        return LIMB_GRAPHICS[randomIndex];
    }

}
