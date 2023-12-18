package com.cryptic.model.map.region;

import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.stream.Collectors;

public class Dev {
    private static final Logger logger = LogManager.getLogger("RegionManager");
    private static final Marker marker = MarkerManager.getMarker("Region");

    public Dev() {
        RegionManager.areasToRegions = (areas) -> {
            Set<Integer> regions = new HashSet();
            Area[] var2 = areas;
            int var3 = areas.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Area area = var2[var4];
                if (area.width() > 63 || area.length() > 63) {
                    for(int x = area.x1; x < area.x2; x += 8) {
                        for(int y = area.y1; y < area.y2; y += 8) {
                            int id = Region.get(x, y).getRegionId();
                            regions.add(id);
                        }
                    }
                }

                regions.add(Region.get(area.x1, area.y1).getRegionId());
                regions.add(Region.get(area.x2, area.y2).getRegionId());
            }

            return regions;
        };
        RegionManager.loadGroupMapFiles = (regions, customZ) -> {
            HashMap<Integer, ArrayList<GameObject>> objs = new HashMap();
            int[] objCount = new int[]{0};
            Iterator var4 = regions.iterator();

            while(var4.hasNext()) {
                int regionId = (Integer)var4.next();
                Region r = RegionManager.getRegion(regionId);
                ArrayList<GameObject> objList = (ArrayList)objs.compute(regionId, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList();
                    }

                    return v;
                });
                long start = System.currentTimeMillis();
                RegionManager.loadMapFiles(r.baseX, r.baseY, true, (objectId, x, y, z, type, direction, r1) -> {
                    GameObject newObj = new GameObject(objectId, new Tile(x, y, customZ + z), type, direction);
                    objList.add(newObj);
                    int var10002 = objCount[0]++;
                    Tile newTile = r.getTile(newObj.x, newObj.y, newObj.z, true);

                    assert newTile != null;

                    newTile.addObject(newObj);
                    newObj.setTile(newTile);
                }, (x, y, z, r1) -> {
                    r1.addClip(x, y, customZ + z, 2097152);
                });
                logger.info("load {} {} {} in {}ms", r.baseX, r.baseY, regionId, System.currentTimeMillis() - start);
            }

            return objCount[0] == 0 ? List.of() : (List)objs.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        };
        Region.provider = new Region.RegionProvider() {
            public Tile getTile(int x, int y, int z, boolean create, Region r) {
                int localX = x - r.baseX;
                int localY = y - r.baseY;
                RegionManager.loadMapFiles(x, y);
                if (z < 0) {
                    throw new RuntimeException("z < 0 : " + z);
                } else if (z > 3) {
                    if (r.customZObjectTiles == null) {
                        if (!create) {
                            return null;
                        }

                        r.customZObjectTiles = new HashMap();
                    }

                    int realLvl = z % 4;
                    int baseZ = z;
                    if (realLvl != 0) {
                        baseZ = z - realLvl;
                    }

                    RegionZData zd = (RegionZData)r.customZObjectTiles.compute(baseZ, (k, v) -> {
                        if (v == null && create) {
                            v = new RegionZData();
                            v.tiles = new Tile[4][64][64];
                        }

                        return v;
                    });
                    if (zd == null) {
                        return null;
                    } else {
                        if (zd.tiles == null) {
                            if (!create) {
                                return null;
                            }

                            zd.tiles = new Tile[4][64][64];
                        }

                        Tile[][][] tiles = zd.tiles;
                        Tile tile = tiles[realLvl][localX][localY];
                        if (tile == null && create) {
                            tile = tiles[realLvl][localX][localY] = new Tile(x, y, z);
                        }

                        return tile;
                    }
                } else {
                    if (r.baseZData.tiles == null) {
                        if (!create) {
                            return null;
                        }

                        r.baseZData.tiles = new Tile[4][64][64];
                    }

                    if (localX > 63 || localY > 63) {
                        //com.dev.Dev.logger.error(com.dev.Dev.marker, "wtf {} {} {} {} {} {} {}", localX, localY, z, r.baseX, r.baseY, x, y);
                    }

                    Tile tilex = r.baseZData.tiles[z][localX][localY];
                    if (tilex == null && create) {
                        tilex = r.baseZData.tiles[z][localX][localY] = new Tile(x, y, z);
                    }

                    return tilex;
                }
            }

            public int getClip(int x, int y, int height, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        return 0;
                    } else {
                        int realLvl = height % 4;
                        int baseZ = height;
                        if (realLvl != 0) {
                            baseZ = height - realLvl;
                        }

                        RegionZData data = r.recentCachedBaseZData;
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ;
                            r.recentCachedBaseZData = data = (RegionZData)r.customZObjectTiles.get(baseZ);
                        }

                        if (data == null) {
                            return 0;
                        } else {
                            if (data.clips[realLvl] == null) {
                                data.clips[realLvl] = new int[64][64];
                            }

                            return data.clips[realLvl][x - regionAbsX][y - regionAbsY];
                        }
                    }
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = new int[64][64];
                    }

                    return r.baseZData.clips[height][x - regionAbsX][y - regionAbsY];
                }
            }

            public int getProjClip(int x, int y, int height, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        return 0;
                    } else {
                        int realLvl = height % 4;
                        int baseZ = height;
                        if (realLvl != 0) {
                            baseZ = height - realLvl;
                        }

                        RegionZData data = r.recentCachedBaseZData;
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ;
                            r.recentCachedBaseZData = data = (RegionZData)r.customZObjectTiles.get(baseZ);
                        }

                        if (data == null) {
                            return 0;
                        } else {
                            if (data.projectileClip[realLvl] == null) {
                                data.projectileClip[realLvl] = new int[64][64];
                            }

                            return data.projectileClip[realLvl][x - regionAbsX][y - regionAbsY];
                        }
                    }
                } else {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = new int[64][64];
                    }

                    return r.baseZData.projectileClip[height][x - regionAbsX][y - regionAbsY];
                }
            }

            public void addClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                int[] var10000;
                if (height <= 3) {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = new int[64][64];
                    }

                    var10000 = r.baseZData.clips[height][x - regionAbsX];
                    var10000[y - regionAbsY] |= shift;
                } else {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = new HashMap();
                    }

                    int realLvl = height % 4;
                    int baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }

                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = (RegionZData)r.customZObjectTiles.compute(baseZ, (k, v) -> {
                            if (v == null) {
                                v = new RegionZData();
                            }

                            return v;
                        });
                    }

                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }

                    var10000 = data.clips[realLvl][x - regionAbsX];
                    var10000[y - regionAbsY] |= shift;
                }
            }

            public void addProjClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                int[] var10000;
                if (height <= 3) {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = new int[64][64];
                    }

                    var10000 = r.baseZData.projectileClip[height][x - regionAbsX];
                    var10000[y - regionAbsY] |= shift;
                } else {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = new HashMap();
                        // com.dev.Dev.logger.trace("region {} z {} created hashmap for clip+object storage. ", r.regionId, height, new Exception("how did you get herre?"));
                    }

                    int realLvl = height % 4;
                    int baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }

                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = (RegionZData)r.customZObjectTiles.compute(baseZ, (k, v) -> {
                            if (v == null) {
                                v = new RegionZData();
                            }

                            return v;
                        });
                    }

                    if (data.projectileClip[realLvl] == null) {
                        data.projectileClip[realLvl] = new int[64][64];
                    }

                    var10000 = data.projectileClip[realLvl][x - regionAbsX];
                    var10000[y - regionAbsY] |= shift;
                }
            }

            public void removeClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                int[] var10000;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                    //    com.dev.Dev.logger.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                    } else {
                        int realLvl = height % 4;
                        int baseZ = height;
                        if (realLvl != 0) {
                            baseZ = height - realLvl;
                        }

                        RegionZData data = r.recentCachedBaseZData;
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ;
                            r.recentCachedBaseZData = data = (RegionZData)r.customZObjectTiles.compute(baseZ, (k, v) -> {
                                if (v == null) {
                                    v = new RegionZData();
                                }

                                return v;
                            });
                        }

                        if (data.clips[realLvl] == null) {
                            data.clips[realLvl] = new int[64][64];
                        }

                        var10000 = data.clips[realLvl][x - regionAbsX];
                        var10000[y - regionAbsY] &= ~shift;
                    }
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = new int[64][64];
                    }

                    var10000 = r.baseZData.clips[height][x - regionAbsX];
                    var10000[y - regionAbsY] &= ~shift;
                }
            }

            public void removeProjClip(int x, int y, int height, int shift, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                int[] var10000;
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                      //  com.dev.Dev.logger.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                    } else {
                        int realLvl = height % 4;
                        int baseZ = height;
                        if (realLvl != 0) {
                            baseZ = height - realLvl;
                        }

                        RegionZData data = r.recentCachedBaseZData;
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ;
                            r.recentCachedBaseZData = data = r.customZObjectTiles.compute(baseZ, (k, v) -> {
                                if (v == null) {
                                    v = new RegionZData();
                                }

                                return v;
                            });
                        }

                        if (data.projectileClip[realLvl] == null) {
                            data.projectileClip[realLvl] = new int[64][64];
                        }

                        var10000 = data.projectileClip[realLvl][x - regionAbsX];
                        var10000[y - regionAbsY] &= ~shift;
                    }
                } else {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = new int[64][64];
                    }

                    var10000 = r.baseZData.projectileClip[height][x - regionAbsX];
                    var10000[y - regionAbsY] &= ~shift;
                }
            }

            public void setClip(int x, int y, int height, int value, Region r) {
                int regionAbsX = (r.regionId >> 8) * 64;
                int regionAbsY = (r.regionId & 255) * 64;
                if (height < 0) {
                    height = 0;
                }

                if (height > 3) {
                    int realLvl = height % 4;
                    int baseZ = height;
                    if (realLvl != 0) {
                        baseZ = height - realLvl;
                    }

                    RegionZData data = r.recentCachedBaseZData;
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ;
                        r.recentCachedBaseZData = data = r.customZObjectTiles.compute(baseZ, (k, v) -> {
                            if (v == null) {
                                v = new RegionZData();
                            }

                            return v;
                        });
                    }

                    if (data.clips[realLvl] == null) {
                        data.clips[realLvl] = new int[64][64];
                    }

                    data.clips[realLvl][x - regionAbsX][y - regionAbsY] = value;
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = new int[64][64];
                    }

                    r.baseZData.clips[height][x - regionAbsX][y - regionAbsY] = value;
                }
            }
        };
    }
}
