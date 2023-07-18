package com.aelous.model.content.raids.theatre.boss.bloat.utils;

import com.aelous.model.content.raids.theatre.boss.bloat.handler.BloatProcess;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.RegionManager;

import java.util.Random;

public class BloatUtils {

    private int getMinX() {
        return Math.min(BloatProcess.BLOAT_AREA.x1(), BloatProcess.BLOAT_AREA.x2());
    }

    private int getMinY() {
        return Math.min(BloatProcess.BLOAT_AREA.y1, BloatProcess.BLOAT_AREA.y2);
    }

    private int getMaxX() {
        return Math.max(BloatProcess.BLOAT_AREA.x1, BloatProcess.BLOAT_AREA.x2);
    }

    private int getMaxY() {
        return Math.max(BloatProcess.BLOAT_AREA.y1, BloatProcess.BLOAT_AREA.y2);
    }

    public Tile getRandomTile() {
        Random random = new Random();
        int minX = getMinX();
        int minY = getMinY();
        int maxX = getMaxX();
        int maxY = getMaxY();

        int randomX = random.nextInt(maxX - minX + 1) + minX;
        int randomY = random.nextInt(maxY - minY + 1) + minY;

        return new Tile(randomX, randomY);
    }
    public boolean isTileValid(Tile tile, Tile randomTile) {
        return MovementQueue.dumbReachable(tile.getX(), tile.getY(), randomTile) && !RegionManager.blocked(randomTile);
    }

    public int getRandomLimbGraphic() {
        Random random = new Random();
        int randomIndex = random.nextInt(BloatProcess.LIMB_GRAPHICS.length);
        return BloatProcess.LIMB_GRAPHICS[randomIndex];
    }

}
