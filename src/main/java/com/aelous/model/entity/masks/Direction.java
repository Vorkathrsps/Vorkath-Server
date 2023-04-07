package com.aelous.model.entity.masks;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

/**
 * Represents a single movement direction.
 * 
 * @author Graham
 */
public enum Direction {

    /**
     * North movement.
     */
    NORTH(0, 1, 1),

    /**
     * East movement.
     */
    EAST(1, 0 , 4),

    /**
     * South movement.
     */
    SOUTH(0, -1, 6),

    /**
     * West movement.
     */
    WEST(-1, 0, 3),

    /**
     * North west movement.
     */
    NORTH_WEST(-1, 1, 0, new Direction[] { WEST, NORTH }),

    /**
     * North east movement.
     */
    NORTH_EAST(1, 1, 2, new Direction[] { EAST, NORTH }),

    /**
     * South east movement.
     */
    SOUTH_EAST(1, -1, 7, new Direction[] { EAST, SOUTH }),

    /**
     * South west movement.
     */
    SOUTH_WEST(-1, -1, 5, new Direction[] { WEST, SOUTH }),

    /**
     * No movement.
     */
    NONE(0, 0, -1);

    /**
     * An empty direction array.
     */
    public static final Direction[] EMPTY_DIRECTION_ARRAY = new Direction[0];

    // // north west
    // // north
    // // north east
    // , west
    // , east
    // // south west
    // // south
    // // south east

    /**
     * Creates a direction from the differences between X and Y.
     *
     * @param deltaX
     *            The difference between two X coordinates.
     * @param deltaY
     *            The difference between two Y coordinates.
     * @return The direction.
     */
    public static Direction fromDeltas(int deltaX, int deltaY) {
        if (deltaY == 1) {
            if (deltaX == 1)
                return Direction.NORTH_EAST;
            else if (deltaX == 0)
                return Direction.NORTH;
            else
                return Direction.NORTH_WEST;
        } else if (deltaY == -1) {
            if (deltaX == 1)
                return Direction.SOUTH_EAST;
            else if (deltaX == 0)
                return Direction.SOUTH;
            else
                return Direction.SOUTH_WEST;
        } else if (deltaX == 1)
            return Direction.EAST;
        else if (deltaX == -1)
            return Direction.WEST;
        return Direction.NONE;
    }

    /**
     * Checks if the direction represented by the two delta values can connect
     * two points together in a single direction.
     *
     * @param deltaX
     *            The difference in X coordinates.
     * @param deltaY
     *            The difference in X coordinates.
     * @return {@code true} if so, {@code false} if not.
     */
    public static boolean isConnectable(int deltaX, int deltaY) {
        return Math.abs(deltaX) == Math.abs(deltaY) || deltaX == 0
                || deltaY == 0;
    }

    public static Direction[] ORTHOGONAL = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * The direction as an integer.
     */
    private final int intValue;
    public int x;
    public int y;
    public static final int GRANULARITY = 2048;
    public boolean isDiagonal;
    public Direction[] neighboring;

    /**
     * Creates the direction.
     *
     * @param intValue
     *            The direction as an integer.
     */
    Direction(int x, int y, int intValue) {
        this(x, y, intValue, null);
    }

    Direction(int x, int y, int value, Direction[] neighbors) {
        this.x = x;
        this.y = y;
        this.intValue = value;
        this.neighboring = neighbors;
        this.isDiagonal = name().contains("_"); // Cheap way to detect diagonal angles but will suffice.
    }

    /**
     * Gets the direction for two offsets, always preferring diagonal
     */
    public static Direction diagonal(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0)
                return SOUTH_WEST;
            else if (dy > 0)
                return NORTH_WEST;
            else
                return WEST;
        } else if (dx > 0) {
            if (dy < 0)
                return SOUTH_EAST;
            else if (dy > 0)
                return NORTH_EAST;
            else
                return EAST;
        } else {
            if (dy < 0)
                return SOUTH;
            else if (dy > 0)
                return NORTH;
        }
        throw new RuntimeException("Not a direction");
    }

    public static final Direction[] values = Direction.values();

    /**
     * Rounds this angle to the nearest direction
     */
    public static Direction of(int angle) {
        return values[(angle + 1024 & 0x3fff) >> 11];
    }

    public static Direction resolveForLargeNpc(Tile tile, NPC n) {
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].test(tile, n)) {
                return Direction.values()[i];
            }
        }
        return NONE;
    }

    public static DirTest[] dirs = new DirTest[] {
        (p, n) -> p.getX() >= n.getX() && p.getX() <= (n.getX()+(n.getSize()-1)) && p.getY() > (n.getY()+(n.getSize()-1)),
        (p, n) -> p.getX() > (n.getX()+ (n.getSize()-1)) && p.getY() >= n.getY() && p.getY() <= (n.getY()+ (n.getSize()-1)),
        (p, n) -> p.getX() >= n.getX() && p.getX() <= n.getX()+(n.getSize()-1) && p.getY() < n.getY(),
        (p, n) -> p.getX() < n.getX() && p.getY() >= n.getY() && p.getY() <= n.getY()+ (n.getSize()-1),

        (p, n) -> p.getX() < n.getX() && p.getY() >= (n.getY() +n.getSize()),
        (p, n) -> p.getX() > n.getX() && p.getY() > n.getY(),
        (p, n) -> p.getX() >= (n.getX()+n.getSize()) && p.getY() < n.getY(),
        (p, n) -> p.getX() < n.getX() && p.getY() < n.getY(),
    };


    @FunctionalInterface
    interface DirTest {
        boolean test(Tile p, NPC n);
    }


    public Direction opposite() {
        return Direction.diagonal(-x, -y);
    }

    /**
     * Base angle for this direction
     */
    public final int angle() {
        return angle(x, y);
    }

    public final Direction rotate(int r) {
        return Direction.of(angle() + r * (GRANULARITY * 2));
    }

    /**
     * Angle for two entities, taking into account their sizes/center
     */
    public static final int angle(Entity from, Entity to) {
        return ((int) (Math.atan2((to.tile().x + (to.getSize() / 2.0)) - (from.tile().x + (from.getSize() / 2.0)), (to.tile().y + (to.getSize() / 2.0)) - (from.tile().y + (from.getSize() / 2.0))) * ((GRANULARITY * 4) / Math.PI))) & (GRANULARITY * 8 - 1);
    }

    public static final int angle(Tile from, Tile to) {
        return ((int) (Math.atan2(to.x - from.x, to.y - from.y) * ((GRANULARITY * 4) / Math.PI))) & (GRANULARITY * 8 - 1);
    }

    public static final int angle(double xOffset, double yOffset) {
        return ((int) (Math.atan2(xOffset, yOffset) * ((GRANULARITY * 4) / Math.PI))) & (GRANULARITY * 8 - 1);
    }

    public static final int angle(int xOffset, int yOffset) {
        return ((int) (Math.atan2(xOffset, yOffset) * ((GRANULARITY * 4) / Math.PI))) & (GRANULARITY * 8 - 1);
    }

    public static Direction getDirection(Tile src, Tile dest) {
        int deltaX = dest.getX() - src.getX();
        int deltaY = dest.getY() - src.getY();
        return getDirection(deltaX, deltaY);
    }

    public static Direction getDirection(int deltaX, int deltaY) {
        if (deltaX != 0)//normalize
            deltaX /= Math.abs(deltaX);
        if (deltaY != 0)
            deltaY /= Math.abs(deltaY);
        for (Direction d : Direction.values()) {
            if (d.x == deltaX && d.y == deltaY)
                return d;
        }
        return null;
    }

    /**
     * Gets the closest direction for two offsets without bias towards diagonal or orthogonal
     */
    public static Direction of(int xOffset, int yOffset) {
        return of(angle(xOffset, yOffset) & 0x3fff);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    /**
     * Gets the direction as an integer which the client can understand.
     *
     * @return The movement as an integer.
     */
    public int toInteger() {
        return intValue;
    }
}
