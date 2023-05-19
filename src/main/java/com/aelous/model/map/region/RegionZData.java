package com.aelous.model.map.region;

import com.aelous.model.map.position.Tile;

public class RegionZData {

    public Tile[][][] tiles;

    /**
     * The clipping in this region.
     */
    public int[][][] clips = new int[4][][];

    public int[][][] projectileClip = new int[4][][];

}
