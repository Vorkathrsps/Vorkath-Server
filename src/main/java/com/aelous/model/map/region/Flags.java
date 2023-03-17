package com.aelous.model.map.region;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Flags {

    /**
     * The flag which denotes a normal tile, no flag.
     */
    public static final int NONE = 0x0;

    /**
     * The flag which denotes a bridge tile.
     */
    public static final int FLOOR_DECORATION = 0x40000;

    /**
     * The flag which denotes a blocked tile.
     */
    public static final int BLOCKED = 0x200000;

    /**
     * The flag for a north facing wall.
     */
    public static final int WALL_NORTH = 0x2;

    /**
     * The flag for a south facing wall.
     */
    public static final int WALL_SOUTH = 0x20;

    /**
     * Object
     */
    public static final int OBJECT = 0x100;

    /**
     * The flag for a east facing wall.
     */
    public static final int WALL_EAST = 0x8;

    /**
     * The flag for a west facing wall.
     */
    public static final int WALL_WEST = 0x80;

    /**
     * The flag for a north east facing wall.
     */
    public static final int WALL_NORTH_EAST = 0x4;

    /**
     * The flag for a north west facing wall.
     */
    public static final int WALL_NORTH_WEST = 0x1;

    /**
     * The flag for a south east facing wall.
     */
    public static final int WALL_SOUTH_EAST = 0x10;

    /**
     * The flag for a south west facing wall.
     */
    public static final int WALL_SOUTH_WEST = 0x40;

    /**
     * The flag for an object occupant, which is impenetrable.
     */
    public static final int FLOOR = 0x20000;

    /**
     * The flag for a impenetrable north facing wall.
     */
    public static final int IMPENETRABLE_WALL_NORTH = 0x400;

    /**
     * The flag for a impenetrable south facing wall.
     */
    public static final int IMPENETRABLE_WALL_SOUTH = 0x4000;

    /**
     * The flag for a impenetrable east facing wall.
     */
    public static final int IMPENETRABLE_WALL_EAST = 0x1000;

    /**
     * The flag for a impenetrable west facing wall.
     */
    public static final int IMPENETRABLE_WALL_WEST = 0x10000;

    /**
     * The flag for a impenetrable north east facing wall.
     */
    public static final int IMPENETRABLE_WALL_NORTH_EAST = 0x800;

    /**
     * The flag for a impenetrable north west facing wall.
     */
    public static final int IMPENETRABLE_WALL_NORTH_WEST = 0x200;

    /**
     * The flag for a impenetrable south east facing wall.
     */
    public static final int IMPENETRABLE_WALL_SOUTH_EAST = 0x2000;

    /**
     * The flag for a impenetrable south west facing wall.
     */
    public static final int IMPENETRABLE_WALL_SOUTH_WEST = 0x8000;

    public static Map<String, Integer> getMASKS() {
        boolean empty = MASKS.isEmpty();
        for (Field declaredField : Flags.class.getDeclaredFields()) {
            if (declaredField.getType() != Integer.class && declaredField.getType() != int.class)
                continue;
            try {
                MASKS.put(declaredField.getName(), declaredField.getInt(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (empty)
            System.out.println("bruh "+Arrays.toString(MASKS.entrySet().stream().map(e->e.getKey()+":"+e.getValue()+",").toArray()));
        return MASKS;
    }

    private static final Map<String,Integer> MASKS = new HashMap<>();

}
