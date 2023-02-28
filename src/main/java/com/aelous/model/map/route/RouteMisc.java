package com.aelous.model.map.route;

import com.aelous.model.entity.Entity;
import com.aelous.model.map.position.Tile;

public class RouteMisc {

    public static int getDistance(Tile src, Tile dest) {
        return getDistance(src.getX(), src.getY(), dest.getX(), dest.getY());
    }

    public static int getDistance(Tile src, int destX, int destY) {
        return getDistance(src.getX(), src.getY(), destX, destY);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        int diffX = Math.abs(x1 - x2);
        int diffY = Math.abs(y1 - y2);
        return Math.max(diffX, diffY);
    }

    public static int getClosestX(Entity src, Entity target) {
        return getClosestX(src, target.tile());
    }

    public static int getClosestX(Entity src, Tile target) {
        int srcX = src.getAbsX();
        int srcSize = src.getSize();
        int targetX = target.getX();

        if (srcSize == 1) {
            return srcX;
        } else {
            int maxRangeX = srcX + srcSize - 1;
            if (targetX < srcX) {
                return srcX;
            } else return Math.min(targetX, maxRangeX);
        }
    }


    public static int getClosestY(Entity src, Entity target) {
        return getClosestY(src, target.tile());
    }

    public static int getClosestY(Entity src, Tile target) {
        int srcAbsY = src.getAbsY();
        int srcSize = src.getSize();
        int targetY = target.getY();

        if (srcSize == 1) {
            return srcAbsY;
        } else {
            int maxRangeX = srcAbsY + srcSize - 1;
            if (targetY < srcAbsY) {
                return srcAbsY;
            } else return Math.min(targetY, maxRangeX);
        }
    }

    public static Tile getClosestPosition(Entity src, Entity target) {
        return new Tile(getClosestX(src, target), getClosestY(src, target), src.getZ());
    }

    public static Tile getClosestPosition(Entity src, Tile target) {
        return new Tile(getClosestX(src, target), getClosestY(src, target), src.getZ());
    }

    public static int getEffectiveDistance(Entity src, Entity target) {
        Tile pos = getClosestPosition(src, target);
        Tile pos2 = getClosestPosition(target, src);
        return getDistance(pos, pos2);
    }
}
