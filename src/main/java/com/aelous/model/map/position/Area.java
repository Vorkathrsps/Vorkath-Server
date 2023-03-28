package com.aelous.model.map.position;

import com.aelous.model.entity.Entity;

import java.util.function.Consumer;

/**
 * Created by Bart on 8/22/2015.
 */
public class Area {

    public final int x1;
    public final int x2;
    public final int y1;
    public final int y2;
    public int level;
    private boolean largeViewPort;

    public Area(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public Area(int x1, int y1, int x2, int y2, int level) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.level = level;
    }

    public Area(int rid) {
        x1 = (rid >> 8) * 64;
        x2 = x1 + 63;
        y1 = (rid & 0xFF) * 64;
        y2 = x1 + 63;
        level = 0;
    }

    public Area(Tile spawnTile, int radius) {
        this(spawnTile.x - radius, spawnTile.y - radius, spawnTile.x + radius, spawnTile.y + radius, spawnTile.level);
    }

    public Area(Tile botleft, Tile topright) {
        this(botleft.x, botleft.y, topright.x, topright.y);
    }

    public Area(Tile botleft, Tile topright, int level) {
        this(botleft.x, botleft.y, topright.x, topright.y, level);
    }

    public Area(Area area) {
        this(area.x1, area.y1, area.x2, area.y2, area.level);
    }

    public Area(Area area, int level) {
        this(area.x1, area.y1, area.x2, area.y2, level);
    }

    public boolean contains(Tile t) {
        return contains(t, false);
    }

    public boolean contains(Tile t, boolean checkZ) {
        if (t.x >= x1 && t.x <= x2 && t.y >= y1 && t.y <= y2) {
            if (checkZ) {
                return t.level == level;
            } else {
                if (level < 3 && t.level < 3) {
                    return true;
                } else {
                    // deal with instances! allow 0-3 on your own instance gap
                    // see InstanceAreaZLimit class for proof
                    int lowerBound = (level / 4) * 4; // 16 /4 = 4th instance, *4 gets lowerBOund 16. if it was 17 it'd still be lowerbound 16.
                    //int zInInstance = level % 4;
                    //System.out.printf("test %s vs %s is lvl %s in %s-%s%n", z1, z2, zInInstance, lowerBound, (lowerBound + 3));
                    //System.out.println(t.level >= lowerBound && t.level <= (lowerBound + 3) ? t.level +" matched "+level : "reject "+t.level+" to "+level);
                    return t.level >= lowerBound && t.level <= (lowerBound + 3);
                }
            }
        }
        return false;
    }

    public boolean containsClosed(Tile t) {
        return containsClosed(t, false);
    }

    public boolean containsClosed(Tile t, boolean checkHeight) {
        return t.x >= x1 && t.x < x2 && t.y >= y1 && t.y < y2 && (!checkHeight || t.level == level);
    }

    public boolean contains(Entity e) {
        return contains(e.tile());
    }

    public boolean contains(Entity e, boolean checkHeight) {
        return contains(e.tile(), checkHeight);
    }

    public int x1() {
        return x1;
    }

    public int x2() {
        return x2;
    }

    public int z1() {
        return y1;
    }

    public int z2() {
        return y2;
    }

    public int level() {
        return level;
    }

    public int width() {
        return x2 - x1;
    }

    public int length() {
        return y2 - y1;
    }

    public Tile center() {
        return new Tile(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2, level);
    }

    public Tile bottomLeft() {
        return new Tile(x1, y1, level);
    }

    public Tile bottomRight() {
        return new Tile(x2, y1, level);
    }

    public Tile topRight() {
        return new Tile(x2, y2, level);
    }

    public Tile topLeft() {
        return new Tile(x1, y2, level);
    }

    public Area enlarge(int tiles) {
        return new Area(x1 - tiles, y1 - tiles, x2 + tiles, y2 + tiles, level);
    }

    public Tile randomTile() {
        double wx = x2 - x1;
        double wz = y2 - y1;
        return new Tile(x1 + (int) Math.round(wx * Math.random()), y1 + (int) Math.round(wz * Math.random()), level);
    }

    public boolean within(Tile other, int size, int distance) {
        if (other == null)
            return false;
        final Tile otherEnd = new Tile(other.getX() + size - 1, other.getY() + size - 1);
        return !(x1 - otherEnd.getX() - distance > 0) && !(x2 - other.getX() + distance < 0) && !(y2 - other.getY() + distance < 0) && !(y1 - otherEnd.getY() - distance > 0);
    }

    public boolean inBounds(Tile p) {
        return p.getX() >= x1 && p.getX() <= x2 && p.getY() >= y1 && p.getY() <= y2;
    }

    public boolean inBounds(int x, int y, int z, int range) {
        return !(this.level != -1 && z != this.level) && x >= x1 - range && x <= x2 + range && y >= y1 - range && y <= y2 + range;
    }

    public Tile relative(Tile tile) {
        return bottomLeft().plus(tile);
    }

    public Area relative(Area area) {
        return new Area(x1 + area.x1, y1 + area.y1, x1 + area.x2, y1 + area.y2, level + area.level);
    }

    public Area toArea() {
        return new Area(x1, y1, x2, y2, level);
    }

    public Area transform(int minX, int minY, int maxX, int maxY, int level) {
        return new Area(this.x1 + minX, this.y1 + minY, this.x2 + maxX, this.y2 + maxY, this.level + level);
    }

    public boolean overlaps(Area other) {
        return Tile.overlaps(x1, y1, x2 - x1 + 1, y2 - y1 + 1, other.x1, other.y1, other.x2 - other.x1 + 1, other.y2 - other.y1 + 1);
    }

    public void forEachPos(Consumer<Tile> consumer) {
        int minZ, maxZ;
        if(level == -1) {
            minZ = 0;
            maxZ = 3;
        } else {
            minZ = level;
            maxZ = minZ;
        }
        for(int z = minZ; z <= maxZ; z++) {
            for(int x = x1; x <= x2; x++) {
                for(int y= y1; y <= y2; y++)
                    consumer.accept(new Tile(x, y, z));
            }
        }
    }

    public Area setLargeViewPort(boolean largeViewPort) {
        this.largeViewPort = largeViewPort;
        return this;
    }

    public boolean largeViewPort() {
        return largeViewPort;
    }

    @Override
    public String toString() {
        return "Area[" + x1 + ".." + y1 + ", " + x2 + ".." + y2 + ", level="+level+"]";
    }
}
