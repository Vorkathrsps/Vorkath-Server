package com.cryptic.model.map.region;

import com.cryptic.cache.definitions.ObjectDefinition;
import com.cryptic.model.map.position.Area;
import com.displee.cache.CacheLibrary;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.displee.cache.index.ReferenceTable;
import com.displee.cache.index.archive.Archive;
import it.unimi.dsi.fastutil.ints.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
@SuppressWarnings("ALL")
public class RegionManager {

    private static final Logger logger = LogManager.getLogger(RegionManager.class);
    public static final int PROJECTILE_NORTH_WEST_BLOCKED = 0x200;
    public static final int PROJECTILE_NORTH_BLOCKED = 0x400;
    public static final int PROJECTILE_NORTH_EAST_BLOCKED = 0x800;
    public static final int PROJECTILE_EAST_BLOCKED = 0x1000;
    public static final int PROJECTILE_SOUTH_EAST_BLOCKED = 0x2000;
    public static final int PROJECTILE_SOUTH_BLOCKED = 0x4000;
    public static final int PROJECTILE_SOUTH_WEST_BLOCKED = 0x8000;
    public static final int PROJECTILE_WEST_BLOCKED = 0x10000;
    public static final int PROJECTILE_TILE_BLOCKED = 0x20000;
    public static final int UNKNOWN = 0x80000;
    public static final int BLOCKED_TILE = 0x200000;
    public static final int UNLOADED_TILE = 0x1000000;
    public static final int OCEAN_TILE = 2097152;

    public static Int2ObjectOpenHashMap<Region> regions = new Int2ObjectOpenHashMap<>();

    public static final Path OSRS = Path.of("data", "cache");
    public static CacheLibrary cache = CacheLibrary.create(String.valueOf(OSRS));
    public static void init() throws Exception {
        var index = cache.index(5);
        index.cache();

        Archive[] archives = index.archives();
        Int2IntMap hashNameToArchive = new Int2IntOpenHashMap(archives.length);
        for (Archive archive : archives) {
            hashNameToArchive.put(archive.getHashName(), archive.getId());
        }

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 256; y++) {
                int landArchiveId = hashNameToArchive.getOrDefault(("l" + x + "_" + y).hashCode(), -1);//index.archiveId("l" + x + "_" + y);
                if (landArchiveId == -1) continue;

                int mapArchiveId = hashNameToArchive.getOrDefault(("m" + x + "_" + y).hashCode(), -1);//index.archiveId("m" + x + "_" + y);
                if (mapArchiveId == -1) continue;

                var regionId = x << 8 | y;

                regions.put(regionId, new Region(regionId, landArchiveId, mapArchiveId));
            }
        }

        logger.info("Loaded {} regions.", regions.size());
    }

    private static byte[] getMapData(int baseX, int baseY, CacheLibrary library) {
        var index = library.index(5);
        int mapArchiveId = index.archiveId("m" + ((baseX >> 3) / 8) + "_" + ((baseY >> 3) / 8));
        return mapArchiveId == -1 ? null : library.data(5, mapArchiveId);
    }

    public static byte[] getLandscapeData(int baseX, int baseY, CacheLibrary library) {
        var index = library.index(5);
        int landArchiveId = index.archiveId("l" + ((baseX >> 3) / 8) + "_" + ((baseY >> 3) / 8));
        return landArchiveId == -1 ? null : library.data(5, landArchiveId);
    }

    /**
     * Attempts to get a {@link Region} based on an id.
     *
     * @param regionId
     * @return
     */
    public static @Nonnull Region getRegion(int regionId) {
        var region = regions.get(regionId);
        if (region == null) {
            region = new Region(regionId, -1, -1);
            regions.put(regionId, region);
        }
        return region;
    }

    /**
     * Attempts to get a {@link Region} based on coordinates.
     */
    public @Nonnull
    static Region getRegion(int x, int y) {
        loadMapFiles(x, y);
        int regionX = x >> 3;
        int regionY = y >> 3;
        int regionId = ((regionX / 8) << 8) + (regionY / 8);
        return getRegion(regionId);
    }

    /**
     * Adds an object to a region.
     *
     * @param objectId
     * @param x
     * @param y
     * @param zLevel
     * @param type
     * @param direction
     */
    public static void addObject(int objectId, int x, int y, int zLevel, int type, int direction) {
        final int oldid = objectId;
        //House portal
        if (x == 2031 && y == 3568) {
            objectId = -1;
        }

        if (objectId == 1391 && x == 3099 && y == 3493) {
            objectId = -1;
        }

        if (objectId == 1123 && x == 3099 && y == 3491) {
            objectId = -1;
        }

        //Pest control doors, they never worked quite right because of instancing.
        if (x == 2670 && y == 2593) {
            objectId = -1;
        }
        if (x == 2670 && y == 2592) {
            objectId = -1;
        }
        if (x == 2657 && y == 2585) {
            objectId = -1;
        }
        if (x == 2656 && y == 2585) {
            objectId = -1;
        }
        if (x == 2643 && y == 2592) {
            objectId = -1;
        }
        if (x == 2643 && y == 2593) {
            objectId = -1;
        }

        if (zLevel == 0) {
            //Wildy altar doors
            if (x == 2958 && y == 3821) {
                objectId = -1;
            }
            if (x == 2958 && y == 3820) {
                objectId = -1;
            }
            //Wildy stairs cave gate 1
            if (x == 3040 && y == 10307) {
                objectId = -1;
            }
            if (x == 3040 && y == 10308) {
                objectId = -1;
            }

            //Wildy stairs cave gate 2
            if (x == 3022 && y == 10311) {
                objectId = -1;
            }
            if (x == 3022 && y == 10312) {
                objectId = -1;
            }

            //Wildy stairs cave gate 3
            if (x == 3044 && y == 10341) {
                objectId = -1;
            }
            if (x == 3044 && y == 10342) {
                objectId = -1;
            }

            //Double doors chaos temple
            if (x == 2958 && y == 3820) {
                objectId = -1;
            }
            if (x == 2958 && y == 3821) {
                objectId = -1;
            }

            switch (objectId) {
                case 1393, 1123, 1391, 29716, 1088, 1015, 1016, 1017, 1018, 307, 356, 357, 358, 1521, 1524 ->
                    objectId = -1;
            }
        }

        /*switch (objectId) {
            case 14233: // pest control gates
            case 14235: // pest control gates
                objectId = -1;
        }*/


        if (objectId == -1) {
            final Tile tile = Tile.get(x, y, zLevel, true);
            //System.out.println("ignoring cache-object on server-side "+ObjectDefinition.forId(oldid).name+" at "+position);
            new GameObject(oldid, new Tile(x, y, zLevel), type, direction).remove();
        } else {
            final Tile tile = Tile.get(x, y, zLevel, true);
            new GameObject(oldid, new Tile(x, y, zLevel), type, direction).spawn();
        }
    }

    /**
     * Attempts to add clipping to a region.
     *
     * @param x
     * @param y
     * @param zLevel
     * @param shift
     */
    public static void addClipping(int x, int y, int zLevel, int shift) {
        Region r = getRegion(x, y);
        Tile.get(x, y, zLevel, true);
        r.addClip(x, y, zLevel, shift);
    }

    public static void addClippingProj(int x, int y, int zLevel, int shift) {
        Region r = getRegion(x, y);
        Tile.get(x, y, zLevel, true);
        r.addClipProj(x, y, zLevel, shift);
    }

    /**
     * Attempts to remove clipping from a region
     *
     * @param x
     * @param y
     * @param zLevel
     * @param shift
     */
    public static void removeClipping(int x, int y, int zLevel, int shift) {
        Region r = getRegion(x, y);
        if (r != null)
            r.removeClip(x, y, zLevel, shift);
    }

    public static void removeClippingProj(int x, int y, int zLevel, int shift) {
        Region r = getRegion(x, y);
        if (r != null)
            r.removeClipProj(x, y, zLevel, shift);
    }

    public static void setClipping(int x, int y, int zLevel, int val) {
        Region r = getRegion(x, y);
        if (r != null)
            r.setClip(x, y, zLevel, val);
    }

    /**
     * Attempts to get the clipping for a region.
     *
     * @param x
     * @param y
     * @param zLevel
     * @return
     */
    public static int getClipping(int x, int y, int zLevel) {
        Region r = getRegion(x, y);
        return r.getClip(x, y, zLevel);
    }

    public static int getClippingProj(int x, int y, int zLevel) {
        Region r = getRegion(x, y);
        return r.getClipProj(x, y, zLevel);
    }

    public static boolean blockedProjectile(Tile tile) {
        return (getClipping(tile.getX(), tile.getY(), tile.getLevel()) & 0x20000) == 0;
    }

    public static boolean blocked(Tile pos) {
        return (getClipping(pos.getX(), pos.getY(), pos.getLevel()) & 0x1280120) != 0;
    }

    public static boolean blockedNorth(Tile pos) {
        return (getClipping(pos.getX(), pos.getY() + 1, pos.getLevel()) & 0x1280120) != 0;
    }

    public static boolean blockedEast(Tile pos) {
        return (getClipping(pos.getX() + 1, pos.getY(), pos.getLevel()) & 0x1280180) != 0;
    }

    public static boolean blockedSouth(Tile pos) {
        return (getClipping(pos.getX(), pos.getY() - 1, pos.getLevel()) & 0x1280102) != 0;
    }

    public static boolean blockedWest(Tile pos) {
        return (getClipping(pos.getX() - 1, pos.getY(), pos.getLevel()) & 0x1280108) != 0;
    }

    public static boolean blockedNorthEast(Tile pos) {
        return (getClipping(pos.getX() + 1, pos.getY() + 1, pos.getLevel()) & 0x12801e0) != 0;
    }

    public static boolean blockedNorthWest(Tile pos) {
        return (getClipping(pos.getX() - 1, pos.getY() + 1, pos.getLevel()) & 0x1280138) != 0;
    }

    public static boolean blockedSouthEast(Tile pos) {
        return (getClipping(pos.getX() + 1, pos.getY() - 1, pos.getLevel()) & 0x1280183) != 0;
    }

    public static boolean blockedSouthWest(Tile pos) {
        return (getClipping(pos.getX() - 1, pos.getY() - 1, pos.getLevel()) & 0x128010e) != 0;
    }

    public static boolean canProjectileMove(int startX, int startY, int endX, int endY, int zLevel, int xLength,
                                            int yLength) {
        int diffX = endX - startX;
        int diffY = endY - startY;
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int ii = 0; ii < max; ii++) {
            int currentX = endX - diffX;
            int currentY = endY - diffY;
            for (int i = 0; i < xLength; i++) {
                for (int i2 = 0; i2 < yLength; i2++) {
                    if (diffX < 0 && diffY < 0) {
                        if ((getClipping(currentX + i - 1, currentY + i2 - 1, zLevel) & (UNLOADED_TILE
                            | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED
                            | PROJECTILE_NORTH_EAST_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0
                            || (getClipping(currentX + i - 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_EAST_BLOCKED)) != 0
                            || (getClipping(currentX + i, currentY + i2 - 1, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_NORTH_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY > 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2 + 1, zLevel) & (UNLOADED_TILE
                            | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED
                            | PROJECTILE_SOUTH_WEST_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0
                            || (getClipping(currentX + i + 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_WEST_BLOCKED)) != 0
                            || (getClipping(currentX + i, currentY + i2 + 1, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_SOUTH_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY > 0) {
                        if ((getClipping(currentX + i - 1, currentY + i2 + 1, zLevel) & (UNLOADED_TILE
                            | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED
                            | PROJECTILE_SOUTH_EAST_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0
                            || (getClipping(currentX + i - 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_EAST_BLOCKED)) != 0
                            || (getClipping(currentX + i, currentY + i2 + 1, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_SOUTH_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY < 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2 - 1, zLevel) & (UNLOADED_TILE
                            | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED
                            | PROJECTILE_NORTH_BLOCKED | PROJECTILE_NORTH_WEST_BLOCKED)) != 0
                            || (getClipping(currentX + i + 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_WEST_BLOCKED)) != 0
                            || (getClipping(currentX + i, currentY + i2 - 1, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_NORTH_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX > 0 && diffY == 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_WEST_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX < 0 && diffY == 0) {
                        if ((getClipping(currentX + i - 1, currentY + i2, zLevel)
                            & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
                            | PROJECTILE_EAST_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY > 0) {
                        if ((getClipping(currentX + i, currentY + i2 + 1, zLevel) & (UNLOADED_TILE
                            | /*
                         * BLOCKED_TILE |
                         */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0) {
                            return false;
                        }
                    } else if (diffX == 0 && diffY < 0) {
                        if ((getClipping(currentX + i, currentY + i2 - 1, zLevel) & (UNLOADED_TILE
                            | /*
                         * BLOCKED_TILE |
                         */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0) {
                            return false;
                        }
                    }
                }
            }
            if (diffX < 0) {
                diffX++;
            } else if (diffX > 0) {
                diffX--;
            }
            if (diffY < 0) {
                diffY++; // change
            } else if (diffY > 0) {
                diffY--;
            }
        }
        return true;
    }

    public static boolean canMove(int startX, int startY, int endX, int endY, int zLevel, int xLength, int yLength) {
        int diffX = endX - startX;
        int diffY = endY - startY;
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int ii = 0; ii < max; ii++) {
            int currentX = endX - diffX;
            int currentY = endY - diffY;
            for (int i = 0; i < xLength; i++) {
                for (int i2 = 0; i2 < yLength; i2++)
                    if (diffX < 0 && diffY < 0) {
                        if ((getClipping((currentX + i) - 1, (currentY + i2) - 1, zLevel) & 0x128010e) != 0
                            || (getClipping((currentX + i) - 1, currentY + i2, zLevel) & 0x1280108) != 0
                            || (getClipping(currentX + i, (currentY + i2) - 1, zLevel) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY > 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2 + 1, zLevel) & 0x12801e0) != 0
                            || (getClipping(currentX + i + 1, currentY + i2, zLevel) & 0x1280180) != 0
                            || (getClipping(currentX + i, currentY + i2 + 1, zLevel) & 0x1280120) != 0)
                            return false;
                    } else if (diffX < 0 && diffY > 0) {
                        if ((getClipping((currentX + i) - 1, currentY + i2 + 1, zLevel) & 0x1280138) != 0
                            || (getClipping((currentX + i) - 1, currentY + i2, zLevel) & 0x1280108) != 0
                            || (getClipping(currentX + i, currentY + i2 + 1, zLevel) & 0x1280120) != 0)
                            return false;
                    } else if (diffX > 0 && diffY < 0) {
                        if ((getClipping(currentX + i + 1, (currentY + i2) - 1, zLevel) & 0x1280183) != 0
                            || (getClipping(currentX + i + 1, currentY + i2, zLevel) & 0x1280180) != 0
                            || (getClipping(currentX + i, (currentY + i2) - 1, zLevel) & 0x1280102) != 0)
                            return false;
                    } else if (diffX > 0 && diffY == 0) {
                        if ((getClipping(currentX + i + 1, currentY + i2, zLevel) & 0x1280180) != 0)
                            return false;
                    } else if (diffX < 0 && diffY == 0) {
                        if ((getClipping((currentX + i) - 1, currentY + i2, zLevel) & 0x1280108) != 0)
                            return false;
                    } else if (diffX == 0 && diffY > 0) {
                        if ((getClipping(currentX + i, currentY + i2 + 1, zLevel) & 0x1280120) != 0)
                            return false;
                    } else if (diffX == 0 && diffY < 0
                        && (getClipping(currentX + i, (currentY + i2) - 1, zLevel) & 0x1280102) != 0)
                        return false;

            }

            if (diffX < 0)
                diffX++;
            else if (diffX > 0)
                diffX--;
            if (diffY < 0)
                diffY++;
            else if (diffY > 0)
                diffY--;
        }

        return true;
    }

    public static boolean canMove(Tile start, Tile end, int xLength, int yLength) {
        return canMove(start.getX(), start.getY(), end.getX(), end.getY(), start.getLevel(), xLength, yLength);
    }

    /**
     * Attemps to load the map files related to this region...
     * this is oss load()
     */

    public static void loadMapFiles(int x, int y) {
        loadMapFiles(x, y, false);
    }

    public static void loadMapFiles(int x, int y, boolean force) {
        loadMapFiles(x, y, force, OBJECT_CONSUMER, CLIP_CONSUMER);
    }

    public static void loadMapFiles(int x, int y, boolean force, ObjectConsumer consumer, ClipConsumer clipConsumer) {
        try {
            int regionX = x >> 3;
            int regionY = y >> 3;
            int regionId = ((regionX / 8) << 8) + (regionY / 8);
            Region r = getRegion(regionId);
            if (r.isLoaded() && !force) return;
            r.setLoaded(true);
            byte[] oFileData = null;
            byte[] gFileData = null;

            if (r.getObjectFile() != -1) gFileData = getMapData(r.baseX, r.baseY, cache);
            if (r.getTerrainFile() != -1) oFileData = getLandscapeData(r.baseX, r.baseY, cache);

            if (gFileData != null) {
                Buffer groundStream = new Buffer(gFileData);
                int absX = (r.getRegionId() >> 8) * 64;
                int absY = (r.getRegionId() & 0xff) * 64;
                r.heightMap = new int[4][64][64];
                for (int z = 0; z < 4; z++) {
                    for (int tileX = 0; tileX < 64; tileX++) {
                        for (int tileY = 0; tileY < 64; tileY++) {
                            while (true) {
                                var tileType = groundStream.getUShort();
                                if (tileType == 0) {
                                    break;
                                } else if (tileType == 1) {
                                    groundStream.getByte();
                                    break;
                                } else if (tileType <= 49) {
                                    groundStream.getShort();
                                } else if (tileType <= 81) {
                                    r.heightMap[z][tileX][tileY] = (byte) (tileType - 49);
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 4; i++) {
                    for (int i2 = 0; i2 < 64; i2++) {
                        for (int i3 = 0; i3 < 64; i3++) {
                            if ((r.heightMap[i][i2][i3] & 1) == 1) {
                                int zLevel = i;
                                if ((r.heightMap[1][i2][i3] & 2) == 2) {
                                    zLevel--;
                                }
                                if (zLevel >= 0 && zLevel <= 3) {
                                    clipConsumer.accept(absX + i2, absY + i3, zLevel, r);
                                }
                            }
                        }
                    }
                }
                if (oFileData != null) {
                    Buffer objectStream = new Buffer(oFileData);
                    int objectId = -1;
                    int incr;
                    while ((incr = objectStream.readUnsignedIntSmartShortCompat()) != 0) {
                        objectId += incr;
                        int location = 0;
                        int incr2;
                        while ((incr2 = objectStream.readUnsignedShortSmart()) != 0) {
                            location += incr2 - 1;
                            int localX = (location >> 6 & 0x3f);
                            int localY = (location & 0x3f);
                            int zLevel = location >> 12;
                            int hash = objectStream.getUByte();
                            int type = hash >> 2;
                            int direction = hash & 0x3;
                            if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) continue;
                            if ((r.heightMap[1][localX][localY] & 2) == 2) zLevel--;
                            if (zLevel >= 0 && zLevel <= 3) consumer.accept(objectId, absX + localX, absY + localY, zLevel, type, direction, r);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("?", e);
            throw new MapDecodeEx("map decode", e);
        }
    }

    public static BiFunction<Set<Integer>, Integer, List<GameObject>> loadGroupMapFiles = (i, i2) -> List.of();
    public static Function<Area[], Set<Integer>> areasToRegions = areas -> Set.of();

    public static final class MapDecodeEx extends RuntimeException {
        public MapDecodeEx(String mapDecode, Exception e) {
            super(mapDecode, e);
        }
    }

    public interface ClipConsumer {
        void accept(int x, int y, int z, Region r);
    }

    public interface ObjectConsumer {
        void accept(int id, int x, int y, int z, int type, int dir, Region r);
    }

    private static final ClipConsumer CLIP_CONSUMER = (x, y, z, r) -> {
        r.addClip(x, y, z, 0x200000);
    };

    private static final ObjectConsumer OBJECT_CONSUMER = (objectId, x, y, z, type, direction, r1) -> {
        var obj = new GameObject(objectId, new Tile(x, y, z), type, direction);
        final Tile tile = r1.getTile(obj.tile().x, obj.tile().y, obj.tile().level, true);
        if (obj.getId() == -1) {
            tile.removeObject(obj);
        } else {
            tile.addObject(obj);
        }
    };

}
