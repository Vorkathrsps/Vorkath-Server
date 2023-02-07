package com.aelous.utility;

/**
 * Created by Bart on 8/25/2015.
 */
public class NpcSpawn {

    public int id;
    public int x;
    public int y;
    public int z;
    public int walkRange;
    public boolean ancientSpawn;
    public String direction;

    public NpcSpawn(int id, int x, int y, int walkRange, String direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.walkRange = walkRange;
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
