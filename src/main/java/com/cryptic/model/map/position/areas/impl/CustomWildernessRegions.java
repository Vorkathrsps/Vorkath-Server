package com.cryptic.model.map.position.areas.impl;

/**
 * This class utilizes all custom wilderness regions.
 *
 * @author Origin
 * December 7, 2021
 */
public enum CustomWildernessRegions {
    CALLISTO_CAVE(7092, 35),
    VETION_CAVE(7604, 35),
    VENENATIS_CAVE(6580, 35);


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
