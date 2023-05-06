package com.aelous.model.map.region;

import com.google.common.base.Stopwatch;
import com.aelous.GameServer;
import com.aelous.GameEngine;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * This manager handles all regions and their related functions, such as
 * clipping.
 *
 * @author Professor Oak
 */
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

    /**
     * The map with all of our regions.
     */
    public static Map<Integer, Region> regions = new HashMap<>();

    /**
     * Loads the client's map_index file and constructs new regions based on the
     * data it holds.
     *
     * @throws Exception
     */
    public static void init() throws Exception {
        // Load regions..
        File map_index = new File(GameServer.properties().clippingDirectory + "map_index.dat");
        if (!map_index.exists()) {
            throw new OperationNotSupportedException("map_index was not found!");
        }
        byte[] data = Files.readAllBytes(map_index.toPath());
        Buffer stream = new Buffer(data);
        int size = stream.getUShort();
        for (int i = 0; i < size; i++) {
            int regionId = stream.getUShort();
            int terrainFile = stream.getUShort();
            int objectFile = stream.getUShort();
            RegionManager.regions.put(regionId, new Region(regionId, terrainFile, objectFile));
        }
    }

    /**
     * Attempts to get a {@link Region} based on an id.
     *
     * @param regionId
     * @return
     */
    public static @Nonnull Region getRegion(int regionId) {
        Region region = regions.get(regionId);
        if (region == null) {
            region = new Region(regionId, -1, -1);
            logger.debug("Region not found in 317 dump {}", regionId );
            regions.put(regionId, region);
        }
        return region;
    }

    /**
     * Attempts to get a {@link Region} based on coordinates.
     */
    public @Nonnull static Region getRegion(int x, int y) {
        loadMapFiles(x, y);
        int regionId = ((x >> 6) << 8) | (y >> 6);
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
                case 1393, 1123, 1391, 29716, 1088, 1015, 1016, 1017, 1018, 307, 356, 357, 358, 1521, 1524 -> objectId = -1;
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
            MapObjects.remove(new GameObject(oldid, new Tile(x,y,zLevel), type, direction));
        } else {
            final Tile tile = Tile.get(x, y, zLevel, true);
            MapObjects.add(new GameObject(oldid, new Tile(x,y,zLevel), type, direction));
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
        zLevel %= 4;
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
        try {
            int regionX = x >> 3;
            int regionY = y >> 3;
            int regionId = ((regionX / 8) << 8) + (regionY / 8);
            Region r = getRegion(regionId);
            if (r.isLoaded() && !force) {
                return;
            }
            r.setLoaded(true);

            Stopwatch stopwatch = Stopwatch.createStarted();
            byte[] oFileData = null;
            byte[] gFileData = null;
            // Attempt to create streams..
            if (r.getObjectFile() != -1)
                oFileData = CompressionUtil.gunzip(
                FileUtil.readFile(GameServer.properties().clippingDirectory + "maps/" + r.getObjectFile() + ".gz"));
            if (r.getTerrainFile() != -1)
                gFileData = CompressionUtil.gunzip(
                FileUtil.readFile(GameServer.properties().clippingDirectory + "maps/" + r.getTerrainFile() + ".gz"));

            // Don't allow ground file to be invalid..
            if (gFileData == null) {
                stopwatch.stop();
                logger.trace("ungzipped clipmap region {} at {} in {} ns but Disregarding Data!", regionId, Tile.regionToTile(regionId), stopwatch.elapsed().toNanos());
                //System.err.println("missing clipping at region "+regionId);
                return;
            }
            if (oFileData != null)
            logger.trace("clipmap region {} at {} in {} ns len:{} len:{} files {} {}", regionId, Tile.regionToTile(regionId), stopwatch.elapsed().toNanos(), oFileData.length, gFileData.length, r.getObjectFile(), r.getTerrainFile());

            // Read values using our streams..
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
                                groundStream.getUByte();
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
                                RegionManager.addClipping(absX + i2, absY + i3, zLevel, 0x200000);
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
                        if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
                            continue;
                        }
                        if ((r.heightMap[1][localX][localY] & 2) == 2) {
                            zLevel--;
                        }

                        if (zLevel >= 0 && zLevel <= 3) {
                            RegionManager.addObject(objectId, absX + localX, absY + localY, zLevel, type, direction); // Add
                        }
                    }
                }
            } else {
                System.err.println("missing mapobjs at region "+regionId);
            }
            stopwatch.stop();
           // if (GameEngine.gameTicksIncrementor > 10) {
                // log when game is running
                //logger.trace("clipmap region {} at {} loaded in {} ns", regionId, Tile.regionToTile(regionId), stopwatch.elapsed().toNanos());
          //  }
        } catch (Exception e) {
            logger.error("?", e);
            throw new MapDecodeEx("map decode", e);
        }
    }

    public static final class MapDecodeEx extends RuntimeException {

        public MapDecodeEx(String mapDecode, Exception e) {
            super(mapDecode, e);
        }
    }
}
