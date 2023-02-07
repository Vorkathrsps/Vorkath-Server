package com.aelous.model.map.position;

import java.util.function.Consumer;

/**
 * Represents a rectangular boundary.
 *
 * @author Professor Oak
 * @author Patrick van Elderen
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class Boundary {

    private int minX;
    private int highX;
    private int minY;
    private int highY;
    private int zLevel;

    /**
     * Sets the boundaries in the constructor
     *
     * @param minX
     *            The south-west x coordinate
     * @param minY
     *            The south-west y coordinate
     * @param highX
     *            The north-east x coordinate
     * @param highY
     *            The north-east y coordinate
     */
    public Boundary(int minX, int minY, int highX, int highY) {
        this.minX = minX;
        this.highX = highX;
        this.minY = minY;
        this.highY = highY;
    }

    /**
     * Sets the boundaries in the constructor
     *
     * @param minX
     *            The south-west x coordinate
     * @param minY
     *            The south-west y coordinate
     * @param highX
     *            The north-east x coordinate
     * @param highY
     *            The north-east y coordinate
     * @param zLevel
     *            The height of the boundary
     */
    public Boundary(int minX, int minY, int highX, int highY, int zLevel) {
        this.minX = minX;
        this.minY = minY;
        this.highX = highX;
        this.highY = highY;
        this.zLevel = zLevel;
    }

    public Boundary(Tile start, int radius) {
        this.minX = start.getX();
        this.minY = start.getY();
        this.highX = minX + radius - 1;
        this.highY = minY + radius - 1;
        this.zLevel = start.getLevel();
    }

    public Boundary(Tile swTile, Tile neTile, int z) {
        this(swTile.x, swTile.y, neTile.x, neTile.y, z);
    }

    public void forEachPos(Consumer<Tile> consumer) {
        int minZ, maxZ;
        if(zLevel == -1) {
            minZ = 0;
            maxZ = 3;
        } else {
            minZ = zLevel;
            maxZ = minZ;
        }
        for(int z = minZ; z <= maxZ; z++) {
            for(int x = minX; x <= getMaximumX(); x++) {
                for(int y= minY; y <= getMaximumY(); y++)
                    consumer.accept(new Tile(x, y, z));
            }
        }
    }

    public int getMinimumX() {
        return minX;
    }

    public int getMinimumY() {
        return minY;
    }

    public int getMaximumX() {
        return highX;
    }

    public int getMaximumY() {
        return highY;
    }

    public int getZ() {
        return zLevel;
    }

    public boolean inside(Tile p) {
        if (p.getX() >= minX && p.getX() <= highX && p.getY() >= minY && p.getY() <= highY) {
            return p.getLevel() == zLevel;
        }
        return false;
    }

    public boolean inBounds(int x, int y, int z, int range) {
        return !(this.zLevel != -1 && z != this.zLevel) && x >= minX - range && x <= highX + range && y >= minY - range && y <= highY + range;
    }

    /**
     * Determines if the other {@link Tile} with it's size is inside the boundary.
     * @param other the other position.
     * @param size the other entity size.
     * @return {@code true} the other entity is inside the boundary, {@code false} otherwise.
     */
    public boolean inside(Tile other, int size) {
        if (other == null)
            return false;
        if (minX == other.getX() && minY == other.getY())
            return true;
        final Tile otherEnd = new Tile(other.getX() + size - 1, other.getY() + size - 1);
        return !(minX - otherEnd.getX() > 0) && !(highX - other.getX() < 0) && !(highY - other.getY() < 0) && !(minY - otherEnd.getY() > 0);
    }

    /**
     * Determines if the other {@link Tile} with it's size is within a distance the boundary.
     * @param other the other position.
     * @param size the other entity size.
     * @return {@code true} the other entity is within the boundary, {@code false} otherwise.
     */
    public boolean within(Tile other, int size, int distance) {
        if (other == null)
            return false;
        final Tile otherEnd = new Tile(other.getX() + size - 1, other.getY() + size - 1);
        return !(minX - otherEnd.getX() - distance > 0) && !(highX - other.getX() + distance < 0) && !(highY - other.getY() + distance < 0) && !(minY - otherEnd.getY() - distance > 0);
    }

    @Override
    public String toString() {
        return "Boundary{" +
            "minX=" + minX +
            ", highX=" + highX +
            ", minY=" + minY +
            ", highY=" + highY +
            ", height=" + zLevel +
            '}';
    }
}
