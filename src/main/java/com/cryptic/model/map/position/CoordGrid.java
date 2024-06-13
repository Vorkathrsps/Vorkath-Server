package com.cryptic.model.map.position;

import java.nio.ByteBuffer;

public final class CoordGrid {

    public static final CoordGrid INVALID = new CoordGrid(-1);

    public final int packed;

    public CoordGrid(int packed) {
        this.packed = packed;
    }

    public CoordGrid(int level, int x, int y) {
        this((level << 28) | (x << 14) | y);
        if (!(level >= 0 && level < 4)) {
            throw new IllegalArgumentException("Level must be in range of 0..<4: " + level);
        }
        if (!(x >= 0 && x <= 16384)) {
            throw new IllegalArgumentException("X coordinate must be in range of 0..<16384: " + x);
        }
        if (!(y >= 0 && y <= 16384)) {
            throw new IllegalArgumentException("Z coordinate must be in range of 0..<16384, " + y);
        }
    }

    public int getLevel() {
        return packed >>> 28;
    }

    public int getX() {
        return (packed >>> 14) & 0x3FFF;
    }

    public int getY() {
        return packed & 0x3FFF;
    }

    public static CoordGrid fromByteArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int packed = buffer.getInt();
        return new CoordGrid(packed);
    }

    public boolean inDistance(CoordGrid other, int distance) {
        if (getLevel() != other.getLevel()) {
            return false;
        }
        int deltaX = getX() - other.getX();
        if (!(deltaX >= -distance && deltaX <= distance)) {
            return false;
        }
        int deltaZ = getY() - other.getY();
        return deltaZ >= -distance && deltaZ <= distance;
    }

    public boolean invalid() {
        return this.equals(INVALID);
    }

    public Tile toTile() {
        return new Tile(getX(), getY(), getLevel());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CoordGrid coordGrid = (CoordGrid) obj;
        return packed == coordGrid.packed;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(packed);
    }

    @Override
    public String toString() {
        return "CoordGrid{" +
            "level=" + getLevel() +
            ", x=" + getX() +
            ", z=" + getY() +
            '}';
    }
}
