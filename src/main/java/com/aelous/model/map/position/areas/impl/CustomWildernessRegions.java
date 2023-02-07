package com.aelous.model.map.position.areas.impl;

/**
 * This class utilizes all custom wilderness regions.
 *
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * December 7, 2021
 */
public enum CustomWildernessRegions {
    SLAYER_TOWER(13623, 30),
    SLAYER_TOWER_BASEMENT(13723, 30),
    CORRUPTED_NECHRYARCH(7484, 30),
    ESCAPE_KEY(13131, 50),
    ESCAPE_KEY2(13387, 50),
    ESCAPE_KEY3(13130, 50),
    ESCAPE_KEY4(13386, 50),
    DEMONIC_GORILLA(8536, 30),
    DEMONIC_GORILLA2(8280, 30),
    SEREN(13118, 57);


    public final int region;
    public final int level;

    CustomWildernessRegions(final int region, final int level){
        this.region = region;
        this.level = level;
    }

    public static CustomWildernessRegions byRegion(int id) {
        for (CustomWildernessRegions customWildernessRegions : CustomWildernessRegions.values()) {
            if(customWildernessRegions.region == id) {
                return customWildernessRegions;//Found region
            }
        }
        //Nothing was found
        return null;
    }
}
