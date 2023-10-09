package com.cryptic.model.content.raids.theatre.boss.bloat.utils;

import com.cryptic.model.content.raids.theatre.boss.bloat.Bloat;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;

import java.util.Random;

public class BloatUtils {

    private int getMinX() {
        return Math.min(Bloat.BLOAT_AREA.x1(), Bloat.BLOAT_AREA.x2());
    }

    private int getMinY() {
        return Math.min(Bloat.BLOAT_AREA.y1, Bloat.BLOAT_AREA.y2);
    }

    private int getMaxX() {
        return Math.max(Bloat.BLOAT_AREA.x1, Bloat.BLOAT_AREA.x2);
    }

    private int getMaxY() {
        return Math.max(Bloat.BLOAT_AREA.y1, Bloat.BLOAT_AREA.y2);
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
        int randomIndex = random.nextInt(Bloat.LIMB_GRAPHICS.length);
        return Bloat.LIMB_GRAPHICS[randomIndex];
    }

}
