package com.aelous.model.map.region;

import com.aelous.model.map.position.Tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Represents a region.
 *
 * @author Professor Oak
 */
public class Region {

    /**
     * This region's id.
     */
    private final int regionId;

    /**
     * This region's terrain file id.
     */
    private final int terrainFile;

    /**
     * This region's object file id.
     */
    private final int objectFile;
    private final int baseX;
    private final int baseY;

    /**
     * The clipping in this region.
     */
    public int[][][] clips = new int[4][][];
    public int[][][] projectileClip = new int[4][][];
    public int[][][] heightMap;

    /**
     * Has this region been loaded?
     */
    private boolean loaded;

    /**
     * Creates a new region.
     *
     * @param regionId
     * @param terrainFile
     * @param objectFile
     */
    public Region(int regionId, int terrainFile, int objectFile) {
        this.regionId = regionId;
        this.terrainFile = terrainFile;
        this.objectFile = objectFile;
        this.baseX = (regionId >> 8) * 64;
        this.baseY = (regionId & 0xff) * 64;
        this.activeTiles = new ArrayList<>();
    }

    public int getRegionId() {
        return regionId;
    }

    public int getTerrainFile() {
        return terrainFile;
    }

    public int getObjectFile() {
        return objectFile;
    }

    /**
     * Gets clipping
     *
     * @param x
     * @param y
     * @param height
     * @return
     */
    public int getClip(int x, int y, int height) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height >3) // normal normal level clip
            height %= 4;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
       /* int finalHeight = height;
        long count = World.getWorld().getPlayers().filter(p -> p != null && p.getPosition().distance(new Position(x, y, finalHeight)) < 10).count();
        if (count > 0)
        Debugs.CLIP.debug(null, String.format("gc %s,%s,%s = %s aka %s%n", x, y, height, clips[height][x - regionAbsX][y - regionAbsY],
            World.clipstr(clips[height][x - regionAbsX][y - regionAbsY])));*/
        return clips[height][x - regionAbsX][y - regionAbsY];
    }

    public int getClipProj(int x, int y, int height) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3)// normal normal level clip
            height %= 4;
        if (projectileClip[height] == null) {
            projectileClip[height] = new int[64][64];
        }
       /* int finalHeight = height;
        long count = World.getWorld().getPlayers().filter(p -> p != null && p.getPosition().distance(new Position(x, y, finalHeight)) < 10).count();
        if (count > 0)
        Debugs.CLIP.debug(null, String.format("gc %s,%s,%s = %s aka %s%n", x, y, height, clips[height][x - regionAbsX][y - regionAbsY],
            World.clipstr(clips[height][x - regionAbsX][y - regionAbsY])));*/
        return projectileClip[height][x - regionAbsX][y - regionAbsY];
    }

    /**
     * Adds clipping
     *
     * @param x
     * @param y
     * @param height
     * @param shift
     */
    public void addClip(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3) // no z>3 support yet
            return;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        // asuming xy is abs xy
        if (x >= 2944 && x<= 3330 && y >= 3521 && y <= 3522) {
            //System.out.println("clip change "+x+", "+y+", "+height+" by "+shift);
            //if (shift == 262144 || shift == 256) {
              //  return; // fuck wildy ditch
          //  }
        }
        clips[height][x - regionAbsX][y - regionAbsY] |= shift;
    }

    public void addClipProj(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3) // no z>3 support yet
            return;
        if (projectileClip[height] == null) {
            projectileClip[height] = new int[64][64];
        }
        projectileClip[height][x - regionAbsX][y - regionAbsY] |= shift;
    }

    /**
     * Removes clipping.
     *
     * @param x
     * @param y
     * @param height
     * @param shift
     */
    public void removeClip(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3) // no z>3 support yet
            return;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        clips[height][x - regionAbsX][y - regionAbsY] &= ~shift;
    }

    public void removeClipProj(int x, int y, int height, int shift) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3) // no z>3 support yet
            return;
        if (projectileClip[height] == null) {
            projectileClip[height] = new int[64][64];
        }
        projectileClip[height][x - regionAbsX][y - regionAbsY] &= ~shift;
    }

    public void setClip(int x, int y, int height, int value) {
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        if (height < 0)
            height = 0;
        if (height > 3) // no z>3 support yet
            return;
        if (clips[height] == null) {
            clips[height] = new int[64][64];
        }
        clips[height][x - regionAbsX][y - regionAbsY] = value;
    }

    /**
     * Gets the local region position.
     *
     * @param tile
     * @return
     */
    public int[] getLocalPosition(Tile tile) {
        int absX = tile.getX();
        int absY = tile.getY();
        int regionAbsX = (regionId >> 8) * 64;
        int regionAbsY = (regionId & 0xff) * 64;
        int localX = absX - regionAbsX;
        int localY = absY - regionAbsY;
        return new int[]{localX, localY};
    }

    public static boolean isInZone(Tile lowerBound, Tile upperBound, Tile userLocation)
    {
        return userLocation.getX() >= lowerBound.getX()
            && userLocation.getX() <= upperBound.getX()
            && userLocation.getY() >= lowerBound.getY()
            && userLocation.getY() <= upperBound.getY()
            && userLocation.getLevel() >= lowerBound.getLevel()
            && userLocation.getLevel() <= upperBound.getLevel();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public final ArrayList<Tile> activeTiles;
    public Tile[][][] tiles;

    public @Nullable Tile getTile(int x, int y, int z, boolean create) {
        if (z > 3)
            return new Tile(x,y,z);// no support for caching z>3 yet
        int localX = x - baseX;
        int localY = y - baseY;
        if(tiles == null) {
            if(!create)
                return null;
            tiles = new Tile[4][64][64];
        }
        Tile tile = tiles[z][localX][localY];
        if(tile == null && create)
            tile = tiles[z][localX][localY] = new Tile(x, y, z);
        return tile;
    }

    public static @Nonnull Region get(int absX, int absY) {
        return RegionManager.getRegion(absX, absY);
    }

}
