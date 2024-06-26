package com.dev.shadow

import com.cryptic.model.map.`object`.GameObject
import com.cryptic.model.map.position.Area
import com.cryptic.model.map.position.Tile
import com.cryptic.model.map.region.Region
import com.cryptic.model.map.region.RegionManager
import com.cryptic.model.map.region.RegionZData
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.util.function.BiFunction
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Jak Shadowrs tardisfan121@gmail.com
 */

object RegionImpl {
    private val regionLogger: org.slf4j.Logger = LoggerFactory.getLogger("RegionManager")
    private val regionMarker: org.slf4j.Marker = MarkerFactory.getMarker("Region")

    @JvmStatic
    fun regioncode() {
        RegionManager.areasToRegions = Function<Array<Area>, Set<Int>> { areas: Array<Area> ->
            val regions: MutableSet<Int> = HashSet()
            val var3 = areas.size

            for (var4 in 0 until var3) {
                val area = areas[var4]
                if (area.width() > 63 || area.length() > 63) {
                    var x = area.x1
                    while (x < area.x2) {
                        var y = area.y1
                        while (y < area.y2) {
                            val id = Region.get(x, y).getRegionId()
                            regions.add(id)
                            y += 8
                        }
                        x += 8
                    }
                }

                regions.add(Region.get(area.x1, area.y1).getRegionId())
                regions.add(Region.get(area.x2, area.y2).getRegionId())
            }
            regions
        }
        RegionManager.loadGroupMapFiles =
            BiFunction<Set<Int>, Int, List<GameObject>> { regions: Set<Int>, customZ: Int ->
                val objs: HashMap<Int, ArrayList<GameObject>> = HashMap<Int, ArrayList<GameObject>>()
                val objCount = intArrayOf(0)
                val var4 = regions.iterator()

                while (var4.hasNext()) {
                    val regionId = var4.next()
                    val r = RegionManager.getRegion(regionId)
                    val objList: ArrayList<GameObject>? = objs.compute(
                        regionId,
                        BiFunction<Int, ArrayList<GameObject>?, ArrayList<GameObject>?> { k: Int, v: ArrayList<GameObject>? ->
                            var v: ArrayList<GameObject>? = v
                            if (v == null) {
                                v = ArrayList<GameObject>()
                            }
                            v
                        })
                    val start = System.currentTimeMillis()
                    RegionManager.loadMapFiles(
                        r.baseX,
                        r.baseY,
                        true,
                        { objectId: Int, x: Int, y: Int, z: Int, type: Int, direction: Int, r1: Region? ->
                            val newObj: GameObject = GameObject(objectId, Tile(x, y, customZ + z), type, direction)
                            objList!!.add(newObj)
                            val var10002 = objCount[0]++
                            val newTile: Tile? = r.getTile(newObj.x, newObj.y, newObj.z, true)

                            newTile!!.addObject(newObj)
                            newObj.setTile(newTile)
                        },
                        { x: Int, y: Int, z: Int, r1: Region ->
                            r1.addClip(x, y, customZ + z, 2097152)
                        })
                    regionLogger.info(
                        regionMarker,
                        "load {} {} {} in {}ms",
                        r.baseX,
                        r.baseY,
                        regionId,
                        System.currentTimeMillis() - start
                    )
                }
                if (objCount[0] == 0) mutableListOf<GameObject>() else objs.values.stream()
                    .flatMap<GameObject>(Function<ArrayList<GameObject>, Stream<out GameObject>> { obj: ArrayList<GameObject> -> obj.stream() })
                    .collect(Collectors.toList())
            }
        Region.provider = object : Region.RegionProvider {
            override fun getTile(x: Int, y: Int, z: Int, create: Boolean, r: Region): Tile? {
                val localX = x - r.baseX
                val localY = y - r.baseY
                RegionManager.loadMapFiles(x, y)
                if (z < 0) {
                    throw RuntimeException("z < 0 : $z")
                } else if (z > 3) {
                    if (r.customZObjectTiles == null) {
                        if (!create) {
                            return null
                        }

                        r.customZObjectTiles = HashMap<Int, RegionZData>()
                    }

                    val realLvl = z % 4
                    var baseZ = z
                    if (realLvl != 0) {
                        baseZ = z - realLvl
                    }

                    val zd: RegionZData? = r.customZObjectTiles.compute(
                        baseZ,
                        BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                            var v: RegionZData? = v
                            if (v == null && create) {
                                v = RegionZData()
                                v!!.tiles =
                                    Array<Array<Array<Tile?>>>(4) { Array<Array<Tile?>>(64) { arrayOfNulls<Tile?>(64) } }
                            }
                            v
                        }) as RegionZData?
                    if (zd == null) {
                        return null
                    } else {
                        if (zd.tiles == null) {
                            if (!create) {
                                return null
                            }

                            zd.tiles =
                                Array<Array<Array<Tile?>>>(4) { Array<Array<Tile?>>(64) { arrayOfNulls<Tile?>(64) } }
                        }

                        val tiles: Array<Array<Array<Tile>>> = zd.tiles
                        var tile: Tile? = tiles[realLvl][localX][localY]
                        if (tile == null && create) {
                            tiles[realLvl][localX][localY] = Tile(x, y, z)
                            tile = tiles[realLvl][localX][localY]
                        }

                        return tile
                    }
                } else {
                    if (r.baseZData.tiles == null) {
                        if (!create) {
                            return null
                        }

                        r.baseZData.tiles =
                            Array<Array<Array<Tile?>>>(4) { Array<Array<Tile?>>(64) { arrayOfNulls<Tile?>(64) } }
                    }

                    if (localX > 63 || localY > 63) {
                        //com.dev.log.error(com.dev.Dev.marker, "wtf {} {} {} {} {} {} {}", localX, localY, z, r.baseX, r.baseY, x, y);
                    }

                    var tilex: Tile? = r.baseZData.tiles[z][localX][localY]
                    if (tilex == null && create) {
                        r.baseZData.tiles[z][localX][localY] = Tile(x, y, z)
                        tilex = r.baseZData.tiles[z][localX][localY]
                    }

                    return tilex
                }
            }

            override fun getClip(x: Int, y: Int, height: Int, r: Region): Int {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        return 0
                    } else {
                        val realLvl = height % 4
                        var baseZ = height
                        if (realLvl != 0) {
                            baseZ = height - realLvl
                        }

                        var data: RegionZData? = r.recentCachedBaseZData
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ
                            data = r.customZObjectTiles[baseZ] as RegionZData?
                            r.recentCachedBaseZData = data
                        }

                        if (data == null) {
                            return 0
                        } else {
                            if (data.clips.get(realLvl) == null) {
                                data.clips[realLvl] = Array(64) { IntArray(64) }
                            }

                            return data.clips.get(realLvl).get(x - regionAbsX).get(y - regionAbsY)
                        }
                    }
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = Array(64) { IntArray(64) }
                    }

                    return r.baseZData.clips[height][x - regionAbsX][y - regionAbsY]
                }
            }

            override fun getProjClip(x: Int, y: Int, height: Int, r: Region): Int {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        return 0
                    } else {
                        val realLvl = height % 4
                        var baseZ = height
                        if (realLvl != 0) {
                            baseZ = height - realLvl
                        }

                        var data: RegionZData? = r.recentCachedBaseZData
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ
                            data = r.customZObjectTiles[baseZ] as RegionZData?
                            r.recentCachedBaseZData = data
                        }

                        if (data == null) {
                            return 0
                        } else {
                            if (data!!.projectileClip.get(realLvl) == null) {
                                data!!.projectileClip[realLvl] = Array(64) { IntArray(64) }
                            }

                            return data!!.projectileClip.get(realLvl).get(x - regionAbsX).get(y - regionAbsY)
                        }
                    }
                } else {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = Array(64) { IntArray(64) }
                    }

                    return r.baseZData.projectileClip[height][x - regionAbsX][y - regionAbsY]
                }
            }

            override fun addClip(x: Int, y: Int, height: Int, shift: Int, r: Region) {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                val var10000: IntArray
                if (height <= 3) {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = Array(64) { IntArray(64) }
                    }

                    var10000 = r.baseZData.clips[height][x - regionAbsX]
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] or shift
                } else {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = HashMap()
                    }

                    val realLvl = height % 4
                    var baseZ = height
                    if (realLvl != 0) {
                        baseZ = height - realLvl
                    }

                    var data: RegionZData? = r.recentCachedBaseZData
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ
                        data = r.customZObjectTiles.compute(
                            baseZ,
                            BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                                var v: RegionZData? = v
                                if (v == null) {
                                    v = RegionZData()
                                }
                                v
                            }) as RegionZData?
                        r.recentCachedBaseZData = data
                    }

                    if (data!!.clips.get(realLvl) == null) {
                        data!!.clips[realLvl] = Array(64) { IntArray(64) }
                    }

                    var10000 = data!!.clips.get(realLvl).get(x - regionAbsX)
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] or shift
                }
            }

            override fun addProjClip(x: Int, y: Int, height: Int, shift: Int, r: Region) {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                val var10000: IntArray
                if (height <= 3) {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = Array(64) { IntArray(64) }
                    }

                    var10000 = r.baseZData.projectileClip[height][x - regionAbsX]
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] or shift
                } else {
                    if (r.customZObjectTiles == null) {
                        r.customZObjectTiles = HashMap<Int, RegionZData>()
                        // com.dev.log.trace("region {} z {} created hashmap for clip+object storage. ", r.regionId, height, new Exception("how did you get herre?"));
                    }

                    val realLvl = height % 4
                    var baseZ = height
                    if (realLvl != 0) {
                        baseZ = height - realLvl
                    }

                    var data: RegionZData? = r.recentCachedBaseZData
                    if (r.recentCachedBaseZLevel != baseZ || data == null) {
                        r.recentCachedBaseZLevel = baseZ
                        data = r.customZObjectTiles.compute(
                            baseZ,
                            BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                                var v: RegionZData? = v
                                if (v == null) {
                                    v = RegionZData()
                                }
                                v
                            }) as RegionZData?
                        r.recentCachedBaseZData = data
                    }

                    if (data!!.projectileClip.get(realLvl) == null) {
                        data!!.projectileClip[realLvl] = Array(64) { IntArray(64) }
                    }

                    var10000 = data!!.projectileClip.get(realLvl).get(x - regionAbsX)
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] or shift
                }
            }

            override fun removeClip(x: Int, y: Int, height: Int, shift: Int, r: Region) {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                val var10000: IntArray
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        //    com.dev.log.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                    } else {
                        val realLvl = height % 4
                        var baseZ = height
                        if (realLvl != 0) {
                            baseZ = height - realLvl
                        }

                        var data: RegionZData? = r.recentCachedBaseZData
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ
                            data = r.customZObjectTiles.compute(
                                baseZ,
                                BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                                    var v: RegionZData? = v
                                    if (v == null) {
                                        v = RegionZData()
                                    }
                                    v
                                }) as RegionZData?
                            r.recentCachedBaseZData = data
                        }

                        if (data!!.clips.get(realLvl) == null) {
                            data!!.clips[realLvl] = Array(64) { IntArray(64) }
                        }

                        var10000 = data!!.clips.get(realLvl).get(x - regionAbsX)
                        var10000[y - regionAbsY] = var10000[y - regionAbsY] and shift.inv()
                    }
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = Array(64) { IntArray(64) }
                    }

                    var10000 = r.baseZData.clips[height][x - regionAbsX]
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] and shift.inv()
                }
            }

            override fun removeProjClip(x: Int, y: Int, height: Int, shift: Int, r: Region) {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                val var10000: IntArray
                if (height > 3) {
                    if (r.customZObjectTiles == null) {
                        //  com.dev.log.trace("region {} z {} removeclip. ", r.regionId, height, new Exception("how did you get herre?"));
                    } else {
                        val realLvl = height % 4
                        var baseZ = height
                        if (realLvl != 0) {
                            baseZ = height - realLvl
                        }

                        var data: RegionZData? = r.recentCachedBaseZData
                        if (r.recentCachedBaseZLevel != baseZ) {
                            r.recentCachedBaseZLevel = baseZ
                            data = r.customZObjectTiles.compute(
                                baseZ,
                                BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                                    var v: RegionZData? = v
                                    if (v == null) {
                                        v = RegionZData()
                                    }
                                    v
                                })
                            r.recentCachedBaseZData = data
                        }

                        if (data!!.projectileClip.get(realLvl) == null) {
                            data!!.projectileClip[realLvl] = Array(64) { IntArray(64) }
                        }

                        var10000 = data!!.projectileClip.get(realLvl).get(x - regionAbsX)
                        var10000[y - regionAbsY] = var10000[y - regionAbsY] and shift.inv()
                    }
                } else {
                    if (r.baseZData.projectileClip[height] == null) {
                        r.baseZData.projectileClip[height] = Array(64) { IntArray(64) }
                    }

                    var10000 = r.baseZData.projectileClip[height][x - regionAbsX]
                    var10000[y - regionAbsY] = var10000[y - regionAbsY] and shift.inv()
                }
            }

            override fun setClip(x: Int, y: Int, height: Int, value: Int, r: Region) {
                var height = height
                val regionAbsX = (r.regionId shr 8) * 64
                val regionAbsY = (r.regionId and 255) * 64
                if (height < 0) {
                    height = 0
                }

                if (height > 3) {
                    val realLvl = height % 4
                    var baseZ = height
                    if (realLvl != 0) {
                        baseZ = height - realLvl
                    }

                    var data: RegionZData? = r.recentCachedBaseZData
                    if (r.recentCachedBaseZLevel != baseZ) {
                        r.recentCachedBaseZLevel = baseZ
                        data = r.customZObjectTiles.compute(
                            baseZ,
                            BiFunction<Int, RegionZData?, RegionZData?> { k: Int?, v: RegionZData? ->
                                var v: RegionZData? = v
                                if (v == null) {
                                    v = RegionZData()
                                }
                                v
                            })
                        r.recentCachedBaseZData = data
                    }

                    if (data!!.clips.get(realLvl) == null) {
                        data!!.clips[realLvl] = Array(64) { IntArray(64) }
                    }

                    data!!.clips.get(realLvl).get(x - regionAbsX)[y - regionAbsY] = value
                } else {
                    if (r.baseZData.clips[height] == null) {
                        r.baseZData.clips[height] = Array(64) { IntArray(64) }
                    }

                    r.baseZData.clips[height][x - regionAbsX][y - regionAbsY] = value
                }
            }
        }
    }
}
