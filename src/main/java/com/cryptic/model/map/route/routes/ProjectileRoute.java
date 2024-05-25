package com.cryptic.model.map.route.routes;

import static com.cryptic.model.map.route.RouteFinder.*;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.Flags;
import com.cryptic.utility.Utils;

public class ProjectileRoute {


    public static boolean hasLineOfSight(Entity entity, Entity target) {
        return hasLineOfSight(
            entity.getAbsX(),
            entity.getAbsY(),
            entity.getZ(),
            entity.getSize(),
            target.getAbsX(),
            target.getAbsY(),
            target.getSize());
    }

    public static boolean hasLineOfSight(Entity entity, Tile dest) {
        return hasLineOfSight(
            entity.getAbsX(),
            entity.getAbsY(),
            entity.getZ(),
            entity.getSize(),
            dest.getX(),
            dest.getY(),
            1);
    }

    public static boolean hasLineOfSight(Entity entity, int destX, int destY) {
        return hasLineOfSight(
            entity.getAbsX(),
            entity.getAbsY(),
            entity.getZ(),
            entity.getSize(),
            destX,
            destY,
            1);
    }

    public static boolean hasLineOfSight(
        int absX, int absY, int z, int size, int targetX, int targetY, int targetSize) {
        targetX = targetX * 2 + targetSize - 1;
        targetY = targetY * 2 + targetSize - 1;

        absX = absX * 2 + size - 1;
        absY = absY * 2 + size - 1;

        if ((targetX & 0x1) != 0) targetX += targetX > absX ? -1 : 1;
        if ((targetY & 0x1) != 0) targetY += targetY > absY ? -1 : 1;

        if ((absX & 0x1) != 0) absX += absX > targetX ? -1 : 1;
        if ((absY & 0x1) != 0) absY += absY > targetY ? -1 : 1;

        return hasLineOfSight(absX >> 1, absY >> 1, z, targetX >> 1, targetY >> 1);
    }

    public static boolean hasLineOfSight(int absX, int absY, int z, int destX, int destY) {
        int dx = Math.abs(destX - absX);
        int dy = Math.abs(destY - absY);
        int sx = absX < destX ? 1 : -1;
        int sy = absY < destY ? 1 : -1;
        int err = dx - dy;
        int err2;
        int oldX = absX;
        int oldY = absY;
        while (true) {
            err2 = err << 1;
            if (err2 > -dy) {
                err -= dy;
                absX += sx;
            }
            if (err2 < dx) {
                err += dx;
                absY += sy;
            }
            if (!allowEntrance(oldX, oldY, z, (absX - oldX), (absY - oldY))) return false;
            if (absX == destX && absY == destY) return true;
            oldX = absX;
            oldY = absY;
        }
    }

    private static boolean allowEntrance(int x, int y, int z, int dx, int dy) {
        if (dx == -1 && dy == 0 && (getClipping(x - 1, y, z) & WEST_MASK) == 0) return true;
        if (dx == 1 && dy == 0 && (getClipping(x + 1, y, z) & EAST_MASK) == 0) return true;
        if (dx == 0 && dy == -1 && (getClipping(x, y - 1, z) & SOUTH_MASK) == 0) return true;
        if (dx == 0 && dy == 1 && (getClipping(x, y + 1, z) & NORTH_MASK) == 0) return true;
        if (dx == -1
            && dy == -1
            && (getClipping(x - 1, y - 1, z) & SOUTH_WEST_MASK) == 0
            && (getClipping(x - 1, y, z) & WEST_MASK) == 0
            && (getClipping(x, y - 1, z) & SOUTH_MASK) == 0) return true;
        if (dx == 1
            && dy == -1
            && (getClipping(x + 1, y - 1, z) & SOUTH_EAST_MASK) == 0
            && (getClipping(x + 1, y, z) & EAST_MASK) == 0
            && (getClipping(x, y - 1, z) & SOUTH_MASK) == 0) return true;
        if (dx == -1
            && dy == 1
            && (getClipping(x - 1, y + 1, z) & NORTH_WEST_MASK) == 0
            && (getClipping(x - 1, y, z) & WEST_MASK) == 0
            && (getClipping(x, y + 1, z) & NORTH_MASK) == 0) return true;
        if (dx == 1
            && dy == 1
            && (getClipping(x + 1, y + 1, z) & NORTH_EAST_MASK) == 0
            && (getClipping(x + 1, y, z) & EAST_MASK) == 0
            && (getClipping(x, y + 1, z) & NORTH_MASK) == 0) return true;
        return false;
    }


    /**
     * Checks whether the projectile would be clipped between the two entities. Size will be called from {@code Entity#getSize()} if the
     * position is an instance of entity, or 1 by default.
     *
     * @param from           the position from which the projectile is shot.
     * @param to             the position to which the projectile is shot.
     * @param closeProximity whether to check for close proximity, meaning objects such as gates would block the path.
     * @return whether the projectile is clipped in this path or not.
     */
    public static boolean isProjectileClipped(final Entity sender, final Entity receiver, final Tile from,
                                              final Tile to,
                                              final boolean closeProximity) {
        final int fromSize = sender != null ? sender.getSize() : 1;
        final int toSize = receiver != null ? receiver.getSize() : 1;
        return isProjectileClipped(sender, receiver, from, to, fromSize, toSize, closeProximity, false);
    }

    public static boolean isProjectileClipped(final Entity sender, final Entity receiver, final Tile from,
                                              final Tile to,
                                              final boolean closeProximity, final boolean ignoreUnderneath) {
        final int fromSize = sender != null ? sender.getSize() : 1;
        final int toSize = receiver != null ? receiver.getSize() : 1;
        return isProjectileClipped(sender, receiver, from, to, fromSize, toSize, closeProximity, ignoreUnderneath);
    }

    public static boolean isProjectileClipped(final Entity sender, final Entity receiver, final Tile from,
                                              final Tile to, final int fromSize, final int toSize, final boolean closeProximity, final boolean ignoreUnderneath) {
        return _isProjectileClipped(sender, receiver, from, to, fromSize, toSize, closeProximity, ignoreUnderneath);
    }

    /**
     * Checks whether the projectile would be clipped between the two entities.
     *
     * @param from             the position from which the projectile is shot.
     * @param to               the position to which the projectile is shot.
     * @param fromSize         size of the from position.
     * @param toSize           size of the to position.
     * @param closeProximity   whether to check for close proximity, meaning objects such as gates would block the path.
     * @param ignoreUnderneath whether or not the code should ignore the from/to tiles.
     * @return whether the projectile is clipped in this path or not.
     */
    private static boolean _isProjectileClipped(final Entity sender, final Entity receiver, final Tile from,
                                                final Tile to, final int fromSize, final int toSize, final boolean closeProximity, boolean ignoreUnderneath) {
        final int fromPlane = from.getZ();
        final int toPlane = to.getZ();
        if (fromPlane != toPlane) {
            return true;
        }
        int cmpThisX, cmpThisY, cmpOtherX, cmpOtherY;
        {
            int otherX = to.getX();
            int otherY = to.getY();
            int thisX = from.getX();
            int thisY = from.getY();


            // Determine which position to compare with for this NPC
            if (otherX <= thisX) {
                cmpThisX = thisX;
            } else if (otherX >= thisX + fromSize - 1) {
                cmpThisX = thisX + fromSize - 1;
            } else {
                cmpThisX = otherX;
            }
            if (otherY <= thisY) {
                cmpThisY = thisY;
            } else if (otherY >= thisY + fromSize - 1) {
                cmpThisY = thisY + fromSize - 1;
            } else {
                cmpThisY = otherY;
            }

            // Determine which position to compare for the other actor
            if (thisX <= otherX) {
                cmpOtherX = otherX;
            } else if (thisX >= otherX + toSize - 1) {
                cmpOtherX = otherX + toSize - 1;
            } else {
                cmpOtherX = thisX;
            }
            if (thisY <= otherY) {
                cmpOtherY = otherY;
            } else if (thisY >= otherY + toSize - 1) {
                cmpOtherY = otherY + toSize - 1;
            } else {
                cmpOtherY = thisY;
            }
        }

        int targetX = cmpOtherX;
        int targetY = cmpOtherY;
        int projX = cmpThisX;
        int projY = cmpThisY;
        int distanceX = targetX - projX;
        int distanceY = targetY - projY;

        if (Utils.collides(from.getX(), from.getY(), fromSize, to.getX(), to.getY(), toSize)) return false;

        int absDistanceX = Math.abs(distanceX);
        int absDistanceY = Math.abs(distanceY);
        if (absDistanceX == 0 && absDistanceY == 0) {
            return false;
        }

        int xTileMask = closeProximity ? (Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.OBJECT) : Flags.OBJECT_PROJECTILE;
        int yTileMask = closeProximity ? (Flags.FLOOR | Flags.FLOOR_DECORATION | Flags.OBJECT) : Flags.OBJECT_PROJECTILE;
        if (distanceX < 0) {
            xTileMask |= closeProximity ? Flags.WALL_EAST : Flags.WALL_EAST_PROJECTILE;
        } else {
            xTileMask |= closeProximity ? Flags.WALL_WEST : Flags.WALL_WEST_PROJECTILE;
        }

        if (distanceY < 0) {
            yTileMask |= closeProximity ? Flags.WALL_NORTH : Flags.WALL_NORTH_PROJECTILE;
        } else {
            yTileMask |= closeProximity ? Flags.WALL_SOUTH : Flags.WALL_SOUTH_PROJECTILE;
        }

        int entityFlag = 0;
        if (sender != null) {
            if (sender instanceof Player) {
                xTileMask |= Flags.OCCUPIED_PROJECTILE_BLOCK_PLAYER;
                yTileMask |= Flags.OCCUPIED_PROJECTILE_BLOCK_PLAYER;
                entityFlag = Flags.OCCUPIED_PROJECTILE_BLOCK_PLAYER;
            } else {
                xTileMask |= Flags.OCCUPIED_PROJECTILE_BLOCK_NPC;
                yTileMask |= Flags.OCCUPIED_PROJECTILE_BLOCK_NPC;
                entityFlag = Flags.OCCUPIED_PROJECTILE_BLOCK_NPC;
            }
        }

        if (absDistanceX > absDistanceY) {
            int currentX = projX;
            int currentY = projY << 16;
            int ratio = (distanceY << 16) / absDistanceX;
            currentY += 32768;
            if (distanceY < 0) {
                --currentY;
            }
            int err = distanceX < 0 ? -1 : 1;
            int actualCurrentY;
            int postRatioCurrentY;
            while (currentX != targetX) {
                currentX += err;
                actualCurrentY = currentY >>> 16;
                if ((getMask(currentX, actualCurrentY, fromPlane) & (currentX == targetX ? (xTileMask & ~entityFlag) :
                    xTileMask)) != 0) {
                    if (!ignoreUnderneath || !((currentX == targetX && actualCurrentY == targetY) || (currentX == projX && actualCurrentY == projY))) {
                        return true;
                    }
                }
                currentY += ratio;
                postRatioCurrentY = currentY >>> 16;
                if (postRatioCurrentY != actualCurrentY
                    && (getMask(currentX, postRatioCurrentY, fromPlane) & (currentX == targetX ? (yTileMask & ~entityFlag)
                    : yTileMask)) != 0) {
                    if (!ignoreUnderneath || !((currentX == targetX && postRatioCurrentY == targetY) || (currentX == projX && postRatioCurrentY == projY))) {
                        return true;
                    }
                }

            }
        } else {
            int currentY = projY;
            int currentX = projX << 16;
            int ratio = (distanceX << 16) / absDistanceY;
            currentX += 32768;
            if (distanceX < 0) {
                --currentX;
            }
            int actualCurrentX;
            int postRatioCurrentX;
            int err = distanceY < 0 ? -1 : 1;
            while (currentY != targetY) {
                currentY += err;
                actualCurrentX = currentX >>> 16;
                if ((getMask(actualCurrentX, currentY, fromPlane) & (currentY == targetY ? (yTileMask & ~entityFlag) :
                    yTileMask)) != 0) {
                    if (!ignoreUnderneath || !((actualCurrentX == targetX && currentY == targetY) || (actualCurrentX == projX && currentY == projY))) {
                        return true;
                    }
                }
                currentX += ratio;
                postRatioCurrentX = currentX >>> 16;
                if (postRatioCurrentX != actualCurrentX && (getMask(postRatioCurrentX, currentY, fromPlane) & (currentY == targetY ? (xTileMask & ~entityFlag) : xTileMask)) != 0) {
                    if (!ignoreUnderneath || !((postRatioCurrentX == targetX && currentY == targetY) || (postRatioCurrentX == projX && currentY == projY))) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private static int getMask(final int x, final int y, final int plane) {
        return RegionManager.getClippingProj(x, y, plane);
    }

    private static int getClipping(int x, int y, int z) {
        return RegionManager.getClippingProj(x, y, z);
    }
}
