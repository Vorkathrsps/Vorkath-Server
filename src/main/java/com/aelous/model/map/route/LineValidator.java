package com.aelous.model.map.route;

import com.aelous.model.map.region.Flags;
import com.aelous.model.map.region.RegionManager;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.model.map.region.RegionManager.*;
import static java.lang.Math.abs;

public class LineValidator {

    private final RegionManager flags;

    public LineValidator(RegionManager flags) {
        this.flags = flags;
    }

    public static boolean hasLineOfSight(
        int level,
        int srcX,
        int srcY,
        int destX,
        int destY,
        int srcSize,
        int destWidth,
        int destHeight
    ) {
        return rayCast(
            level,
            srcX,
            srcY,
            destX,
            destY,
            srcSize,
            destWidth,
            destHeight,
            SIGHT_BLOCKED_WEST,
            SIGHT_BLOCKED_EAST,
            SIGHT_BLOCKED_SOUTH,
            SIGHT_BLOCKED_NORTH,
            true
        );
    }

    public static boolean hasLineOfSight(int level, int srcX, int srcY, int destX, int destY) {
        return hasLineOfSight(level, srcX, srcY, destX, destY, 1, 0, 0);
    }

    public static boolean hasLineOfWalk(
        int level,
        int srcX,
        int srcY,
        int destX,
        int destY,
        int srcSize,
        int destWidth,
        int destHeight
    ) {
        return rayCast(
            level,
            srcX,
            srcY,
            destX,
            destY,
            srcSize,
            destWidth,
            destHeight,
            WALK_BLOCKED_WEST,
            WALK_BLOCKED_EAST,
            WALK_BLOCKED_SOUTH,
            WALK_BLOCKED_NORTH,
            false
        );
    }

    public static boolean hasLineOfWalk(
        int level,
        int srcX,
        int srcY,
        int destX,
        int destY
    ) {
        return hasLineOfWalk(level, srcX, srcY, destX, destY, 1, 0, 0);
    }

    private static boolean rayCast(
        int level,
        int srcX,
        int srcY,
        int destX,
        int destY,
        int srcSize,
        int destWidth,
        int destHeight,
        int flagWest,
        int flagEast,
        int flagSouth,
        int flagNorth,
        boolean los
    ) {
        int startX = coordinate(srcX, destX, srcSize);
        int startY = coordinate(srcY, destY, srcSize);

        if (los && isFlagged( startX, startY, level, Flags.OBJECT)) {
            return false;
        }

        int endX = coordinate(destX, srcX, destWidth);
        int endY = coordinate(destY, srcY, destHeight);

        if (startX == endX && startY == endY) {
            return true;
        }

        int deltaX = endX - startX;
        int deltaY = endY - startY;

        boolean travelEast = deltaX >= 0;
        boolean travelNorth = deltaY >= 0;

        int xFlags = travelEast ? flagWest : flagEast;
        int yFlags = travelNorth ? flagSouth : flagNorth;

        if (abs(deltaX) > abs(deltaY)) {
            int offsetX = travelEast ? 1 : -1;
            int offsetY = travelNorth ? 0 : -1;

            int scaledY = scaleUp(startY) + HALF_TILE + offsetY;
            int tangent = scaleUp(deltaY) / abs(deltaX);

            int currX = startX;
            while (currX != endX) {
                currX += offsetX;
                int currY = scaleDown(scaledY);

                if (los && currX == endX && currY == endY) xFlags = xFlags & ~PROJECTILE_TILE_BLOCKED;
                if (isFlagged( currX, currY, level, xFlags)) {
                    return false;
                }

                scaledY += tangent;

                int nextY = scaleDown(scaledY);
                if (los && currX == endX && nextY == endY) yFlags = yFlags & ~PROJECTILE_TILE_BLOCKED;
                if (nextY != currY && isFlagged(currX, currY, level, yFlags)) {
                    return false;
                }
            }
        } else {
            int offsetX = travelEast ? 0 : -1;
            int offsetY = travelNorth ? 1 : -1;

            int scaledX = scaleUp(startX) + HALF_TILE + offsetX;
            int tangent = scaleUp(deltaX) / abs(deltaY);

            int currY = startY;
            while (currY != endY) {
                currY += offsetY;
                int currX = scaleDown(scaledX);
                if (los && currX == endX && currY == endY) yFlags = yFlags & ~PROJECTILE_TILE_BLOCKED;
                if (isFlagged( currX, currY, level, yFlags)) {
                    return false;
                }

                scaledX += tangent;

                int nextX = scaleDown(scaledX);
                if (los && nextX == endX && currY == endY) xFlags = xFlags & ~PROJECTILE_TILE_BLOCKED;
                if (nextX != currX && isFlagged( nextX, currY, level, xFlags)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int coordinate(int a, int b, int size) {
        if (a >= b) {
            return a;
        } else if (a + size - 1 <= b) {
            return a + size - 1;
        } else {
            return b;
        }
    }

    private static boolean isFlagged(int x, int y, int level, int testFlags) {
        return (getClippingProj(x, y, level) & testFlags) != 0;
    }

    private static final int SIGHT_BLOCKED_NORTH = PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED;
    private static final int SIGHT_BLOCKED_EAST = PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED;
    private static final int SIGHT_BLOCKED_SOUTH = PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED;
    private static final int SIGHT_BLOCKED_WEST = PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED;

    private static final int WALK_BLOCKED_NORTH =
        Flags.WALL_NORTH | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
    private static final int WALK_BLOCKED_EAST =
        Flags.WALL_EAST | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
    private static final int WALK_BLOCKED_SOUTH =
        Flags.WALL_SOUTH | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
    private static final int WALK_BLOCKED_WEST =
        Flags.WALL_WEST | Flags.FLOOR_DECORATION | Flags.OBJECT | Flags.FLOOR;
    private static final int SCALE = 16;
    private static final int HALF_TILE = scaleUp(1) / 2;

    private static int scaleUp(int tiles) {
        return tiles << SCALE;
    }

    private static int scaleDown(int tiles) {
        return tiles >>> SCALE;
    }
}
