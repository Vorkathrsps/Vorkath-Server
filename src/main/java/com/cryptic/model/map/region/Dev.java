package com.cryptic.model.map.region;

import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;

public class Dev {
    private static final Logger logger = LogManager.getLogger("RegionManager");
    private static final Marker marker = MarkerManager.getMarker("Region");

    public Dev() {
        RegionManager.areasToRegions = areas -> {
            IntOpenHashSet regions = IntOpenHashSet.of();
            for (Area area : areas) regions.add(area.middleTile().region());
            return regions;
        };
        RegionManager.loadGroupMapFiles = (regions, customZ) -> {
            var objects = new Int2ObjectOpenHashMap<ArrayList<GameObject>>();
            for (int regionId : regions) {
                var region = RegionManager.getRegion(regionId);
                var objectList = objects.computeIfAbsent(regionId, k -> new ArrayList<>());
                long start = System.currentTimeMillis();
                RegionManager.loadMapFiles(region.baseX, region.baseY, true, (objectId, x, y, z, type, direction, r1) -> {
                    var object = new GameObject(objectId, new Tile(x, y, customZ + z), type, direction);
                    objectList.add(object);
                    var customZTile = region.getTile(object.x, object.y, object.z, true);
                    assert customZTile != null;
                    customZTile.addObject(object);
                    object.setTile(customZTile);
                }, (x, y, z, r1) -> r1.addClip(x, y, customZ + z, 0x200000));
                logger.info("Region Data Loaded: [Objects Loaded: {}] [Region BaseX: {}] [Region BaseY: {}] [Region ID: {}] in [Time: {} MS]", objectList.size(), region.baseX, region.baseY, regionId, System.currentTimeMillis() - start);
            }
            return objects.int2ObjectEntrySet().fastIterator().next().getValue().stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        };
        Region.provider = new Region.RegionProvider() {
            @Override
            public Tile getTile(int x, int y, final int z, boolean create, Region r) {
                int localX = x - r.baseX;
                int localY = y - r.baseY;
                RegionManager.loadMapFiles(x, y);
                if (z < 0)
                    throw new RuntimeException("z < 0 : " + z);
                if (z > 3) {
                    if (r.customZObjectTiles == null) {
                        if (!create)
                            return null;
                        r.customZObjectTiles = new Int2ObjectOpenHashMap<>();
                    }
                    var realLvl = z % 4;
                    var baseZ = z;
                    if (realLvl != 0) {
                        // level 6 becomes 6-2 = 4. base z achieved.
                        baseZ = z - realLvl;
                    }
                    RegionZData zd = r.customZObjectTiles.compute(baseZ, (k, v) -> {
                        if (v == null && create) {
                            v = new RegionZData();
                            v.tiles = new Tile[4][64][64];
                        }
                        return v;
                    });
                    if (zd == null)
                        return null;
                    if (zd.tiles == null) {
                        if (!create)
                            return null;
                        zd.tiles = new Tile[4][64][64];
                    }
                    Tile[][][] tiles = zd.tiles;
                    Tile tile = tiles[realLvl][localX][localY];
                    if (tile == null && create)
                        tile = tiles[realLvl][localX][localY] = new Tile(x, y, z);
                    // else if (tile != null)
                    //     logger.info("found {} at custom z {} nornmalized from real:{} base:{}", tile, z, customZ, baseZ);
                    return tile;
                }
                if (r.baseZData.tiles == null) {
                    if (!create)
                        return null;
                    r.baseZData.tiles = new Tile[4][64][64];
                }
                if (localX > 63 || localY > 63) {
                    logger.error(marker, "wtf {} {} {} {} {} {} {}", localX, localY, z, r.baseX, r.baseY, x, y);
                }
                Tile tile = r.baseZData.tiles[z][localX][localY];
                if (tile == null && create)
                    tile = r.baseZData.tiles[z][localX][localY] = new Tile(x, y, z);
                //if (x == 3471 && y == 5773)
                //    logger.info("tile result {}", tile);
                return tile;
            }

            @Override
            public int getClip(int x, int y, int height, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) // tile not created. must be created first.
                        return 0;
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.get(baseZ);
                    }
                    if (data == null)
                        return 0;
                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }
                    return data.clips[realLvl][x - regionAbsX][y - regionAbsY];
                }
                if (r.baseZData.clips[height] == null) {
                    r.baseZData.clips[height] = new int[64][64];
                }
       /* int finalHeight = height;
        long count = World.getWorld().getPlayers().filter(p -> p != null && p.getPosition().distance(new Position(x, y, finalHeight)) < 10).count();
        if (count > 0)
        Debugs.CLIP.debug(null, String.format("gc %s,%s,%s = %s aka %s%n", x, y, height, clips[height][x - regionAbsX][y - regionAbsY],
            World.clipstr(clips[height][x - regionAbsX][y - regionAbsY])));*/
                return r.baseZData.clips[height][x - regionAbsX][y - regionAbsY];
            }

            @Override
            public int getProjClip(int x, int y, int height, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) // tile not created. must be created first.
                        return 0;
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.get(baseZ);
                    }
                    if (data == null)
                        return 0;
                    if (data.projectileClip[realLvl] == null) {
                        data.projectileClip[realLvl] = new int[64][64];
                    }
                    return data.projectileClip[realLvl][x - regionAbsX][y - regionAbsY];
                }
                if (r.baseZData.projectileClip[height] == null) {
                    r.baseZData.projectileClip[height] = new int[64][64];
                }
       /* int finalHeight = height;
        long count = World.getWorld().getPlayers().filter(p -> p != null && p.getPosition().distance(new Position(x, y, finalHeight)) < 10).count();
        if (count > 0)
        Debugs.CLIP.debug(null, String.format("gc %s,%s,%s = %s aka %s%n", x, y, height, clips[height][x - regionAbsX][y - regionAbsY],
            World.clipstr(clips[height][x - regionAbsX][y - regionAbsY])));*/
                return r.baseZData.projectileClip[height][x - regionAbsX][y - regionAbsY];
            }

            @Override
            public void addClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = new Int2ObjectOpenHashMap<>();
                        // logger.trace("region {} z {} created hashmap for clip+object storage. ", r.regionId, height, new Exception("how did you get herre?"));
                    }
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.computeIfAbsent(baseZ, k -> new RegionZData());
                        logger.info("compute z:{} to base:{} z:{}, data:{}", height, baseZ, realLvl, data);
                    }
                    // logger.info("addClip z:{} to base:{} z:{} cachedZ:{} found:{}", height, baseZ, realLvl, r.recentCachedBaseZLevel, data);
                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }
                    data.clips[realLvl][x - regionAbsX][y - regionAbsY] |= shift;
                    return;
                }
                if (r.baseZData.clips[height] == null) {
                    r.baseZData.clips[height] = new int[64][64];
                }
                // assuming xy is abs xy
                r.baseZData.clips[height][x - regionAbsX][y - regionAbsY] |= shift;
            }

            @Override
            public void addProjClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = new Int2ObjectOpenHashMap<>();
                        logger.trace("region {} z {} created hashmap for clip+object storage. ", r.regionId, height, new Exception("how did you get herre?"));
                    }
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.computeIfAbsent(baseZ, k -> new RegionZData());
                    }
                    if (data.projectileClip[realLvl] == null) {
                        data.projectileClip[realLvl] = new int[64][64];
                    }
                    data.projectileClip[realLvl][x - regionAbsX][y - regionAbsY] |= shift;
                    return;
                }
                if (r.baseZData.projectileClip[height] == null) {
                    r.baseZData.projectileClip[height] = new int[64][64];
                }
                r.baseZData.projectileClip[height][x - regionAbsX][y - regionAbsY] |= shift;
            }

            @Override
            public void removeClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        logger.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                        return;
                    }
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.computeIfAbsent(baseZ, k -> new RegionZData());
                    }
                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }
                    data.clips[realLvl][x - regionAbsX][y - regionAbsY] &= ~shift;
                    return;
                }
                if (r.baseZData.clips[height] == null) {
                    r.baseZData.clips[height] = new int[64][64];
                }
                r.baseZData.clips[height][x - regionAbsX][y - regionAbsY] &= ~shift;
            }

            @Override
            public void removeProjClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        logger.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                        return;
                    }
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.computeIfAbsent(baseZ, k -> new RegionZData());
                    }
                    if (data.projectileClip[realLvl] == null) {
                        data.projectileClip[realLvl] = new int[64][64];
                    }
                    data.projectileClip[realLvl][x - regionAbsX][y - regionAbsY] &= ~shift;
                    return;
                }
                if (r.baseZData.projectileClip[height] == null) {
                    r.baseZData.projectileClip[height] = new int[64][64];
                }
                r.baseZData.projectileClip[height][x - regionAbsX][y - regionAbsY] &= ~shift;
            }

            @Override
            public void setClip(int x, int y, int height, int value, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 0xff) * 64;
                if (height < 0)
                    height = 0;
                if (height > 3) {
                    var realLvl = height % 4;
                    var baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }
                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.computeIfAbsent(baseZ, k -> new RegionZData());
                    }
                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }
                    data.clips[realLvl][x - regionAbsX][y - regionAbsY] = value;
                    return;
                }
                if (r.baseZData.clips[height] == null) {
                    r.baseZData.clips[height] = new int[64][64];
                }
                r.baseZData.clips[height][x - regionAbsX][y - regionAbsY] = value;
            }
        };
    }
}
