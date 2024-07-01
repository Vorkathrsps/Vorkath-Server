package com.cryptic.tools

import com.cryptic.GameServer
import com.cryptic.ServerType
import com.cryptic.getCacheLocation
import com.cryptic.getRawCacheLocation
import com.cryptic.tools.custom.PackDats
import com.cryptic.tools.custom.PackSpritesCustom
import dev.openrune.cache.tools.Builder
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.tools.tasks.TaskType
import dev.openrune.cache.tools.tasks.impl.PackMaps
import dev.openrune.cache.tools.tasks.impl.PackModels
import dev.openrune.cache.tools.tasks.impl.PackSprites
import dev.openrune.cache.tools.tasks.impl.RemoveXteas
import dev.openrune.cache.tools.tasks.impl.defs.PackConfig
import dev.openrune.cache.tools.tasks.impl.defs.PackMode
import java.io.File
import kotlin.system.exitProcess

object CacheTools {

    fun initJs5Server() {
        val js5Server = Builder(
            type = TaskType.RUN_JS5,
            revision = 219,
            cacheLocation = File(getCacheLocation()),
            js5Ports = listOf(43595)
        )
        js5Server.build().initialize()
    }

}

fun main(args : Array<String>) {
    if (args.size < 2) {
        println("Usage: <buildType> <serverName>")
        exitProcess(1)
    }

    val type = args[0]
    val serverName = args[1]
    val rev = 221

    // Validate build type
    if (type != "update" && type != "build") {
        println("Invalid build type. Use 'update' or 'build'.")
        exitProcess(1)
    }

    // Validate server name
    if (!isValidServerName(serverName)) {
        println("Invalid server name: $serverName")
        exitProcess(1)
    }

    GameServer.serverType = ServerType.valueOf(serverName.uppercase())

    val tasks : MutableList<CacheTask> = listOf(
        PackSprites(spritesDirectory = getRawCacheLocation("osrs_sprites/")),
        PackSpritesCustom(getRawCacheLocation("sprites/")),
        PackDats(getRawCacheLocation("dats/")),
        PackModels(getRawCacheLocation("models/")),
        PackConfig(PackMode.ITEMS,getRawCacheLocation("definitions/items/")),
        PackConfig(PackMode.OBJECTS, getRawCacheLocation("definitions/objects/")),
        PackConfig(PackMode.NPCS, getRawCacheLocation("definitions/npcs/"))
    ).toMutableList()

    when(GameServer.serverType) {
        ServerType.VORKATH -> {
            tasks.add(PackMaps(getRawCacheLocation("Vorkath/maps/")))
            tasks.add(PackConfig(PackMode.OBJECTS,getRawCacheLocation("Vorkath/objects/")))
        }
        ServerType.VARLAMORE -> {
            tasks.add(PackMaps(getRawCacheLocation("Varlamore/maps/")))
        }
    }

    when(type) {
        "update" -> {
            val builder = Builder(type = TaskType.FRESH_INSTALL, revision = rev, File(getCacheLocation()))
            builder.extraTasks(*tasks.toTypedArray(), RemoveXteas(File(getCacheLocation(),"xteas.json"))).build().initialize()
        }
        "build" -> {
            val builder = Builder(type = TaskType.BUILD, revision = rev, File(getCacheLocation()))
            builder.extraTasks(*tasks.toTypedArray()).build().initialize()
        }
    }
}

fun isValidServerName(serverName: String): Boolean {
    return ServerType.entries.any { it.name == serverName }
}

