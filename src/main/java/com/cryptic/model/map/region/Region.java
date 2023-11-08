package com.cryptic.model.map.region;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Boundary;
import com.cryptic.model.map.position.Tile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a region.
 *
 * @author Professor Oak
 */
@SuppressWarnings("ALL")
public class Region {
    private static final Logger logger = LogManager.getLogger(Region.class);
    private static final Marker marker = MarkerManager.getMarker("Region");

    /**
     * This region's id.
     */
    public final int regionId;

    /**
     * This region's terrain file id.
     */
    public final int terrainFile;

    /**
     * This region's object file id.
     */
    public final int objectFile;
    public final int baseX;
    public final int baseY;
    public int[][][] heightMap;

    /**
     * Has this region been loaded?
     */
    private boolean loaded;
    @Getter public ArrayList<NPC> npcs;
    @Getter public final ArrayList<Player> players;
    public final Boundary bounds;

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
        this.npcs = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bounds = Boundary.fromRegion(regionId);
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

    @Nullable
    public Tile getTile(int x, int y, final int z, boolean create) {
        return provider.getTile(x, y, z, create, this);
    }

    /**
     * Gets clipping
     */
    public int getClip(int x, int y, int height) {
        return provider.getClip(x, y, height, this);
    }

    public int getClipProj(int x, int y, int height) {
        return provider.getProjClip(x, y, height, this);
    }

    /**
     * Adds clipping
     */
    public void addClip(int x, int y, int height, int shift) {
        provider.addClip(x, y, height, shift, this);
    }

    public void addClipProj(int x, int y, int height, int shift) {
        provider.addProjClip(x, y, height, shift, this);
    }

    /**
     * Removes clipping.
     */
    public void removeClip(int x, int y, int height, int shift) {
        provider.removeClip(x, y, height, shift, this);
    }

    public void removeClipProj(int x, int y, int height, int shift) {
        provider.removeProjClip(x, y, height, shift, this);
    }

    public void setClip(int x, int y, int height, int value) {
        provider.setClip(x, y, height, value, this);
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

    public static void update(Player player) {
        for(Region region : player.getRegions()) {
            for(Tile tile : region.activeTiles) {
                tile.update(player);
            }
        }
    }

    public static @Nonnull Region get(int absX, int absY) {
        return RegionManager.getRegion(absX, absY);
    }

    public interface RegionProvider {
        Tile getTile(int x, int y, final int z, boolean create, Region r);
        int getClip(int x, int y, int height, Region r);
        int getProjClip(int x, int y, int height, Region r);
        void addClip(int x, int y, int height, int shift, Region r);
        void addProjClip(int x, int y, int height, int shift, Region r);
        void removeClip(int x, int y, int height, int shift, Region r);
        void removeProjClip(int x, int y, int height, int shift, Region r);
        void setClip(int x, int y, int height, int value, Region r);

    }

    public static RegionProvider provider = new RegionProvider() {
        @Override
        public Tile getTile(int x, int y, int z, boolean create, Region r) {
            return new Tile(x, y, z);
        }

        @Override
        public int getClip(int x, int y, int height, Region r) {
            return 0;
        }

        @Override
        public int getProjClip(int x, int y, int height, Region r) {
            return 0;
        }

        @Override
        public void addClip(int x, int y, int height, int shift, Region r) {

        }

        @Override
        public void addProjClip(int x, int y, int height, int shift, Region r) {

        }

        @Override
        public void removeClip(int x, int y, int height, int shift, Region r) {

        }

        @Override
        public void removeProjClip(int x, int y, int height, int shift, Region r) {

        }

        @Override
        public void setClip(int x, int y, int height, int value, Region r) {

        }
    };

    public Int2ObjectOpenHashMap<RegionZData> customZObjectTiles;
    public RegionZData baseZData = new RegionZData();
    public int recentCachedBaseZLevel;
    public RegionZData recentCachedBaseZData;

}
