package com.cryptic.utility;

public class NpcSpawn {

    public int x;
    public int y;
    public int z;
    public int id;
    public int walkRange;
    public String direction;

    public NpcSpawn(int x, int y, int z, int id, int walkRange, String direction) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.walkRange = walkRange;
        this.direction = direction;
    }

    public NpcSpawn() {}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWalkRange() {
        return walkRange;
    }

    public void setWalkRange(int walkRange) {
        this.walkRange = walkRange;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int dir() {
        if (direction != null) {
            switch (direction.toLowerCase()) {
                case "s":
                    return 6;
                case "nw":
                    return 0;
                case "n":
                    return 1;
                case "ne":
                    return 2;
                case "w":
                    return 3;
                case "e":
                    return 4;
                case "sw":
                    return 5;
                case "se":
                    return 7;
            }
        }
        return 1;
    }

}
