package com.cryptic.tools

import com.cryptic.GameServer
import com.cryptic.ServerSettingsManager
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
import dev.openrune.cache.tools.tasks.impl.defs.json.PackItems
import java.io.File

object CacheTools {

    fun initJs5Server() {
        val js5Server = Builder(
            type = TaskType.RUN_JS5,
            revision = 219,
            cacheLocation = File(GameServer.settings().getCacheLocation()),
            js5Ports = listOf(43595)
        )
        js5Server.build().initialize()
    }

}

fun main(args : Array<String>) {
    val type = args.first()
    ServerSettingsManager.init()
    val rev = 221
    val settings = ServerSettingsManager.settings

    val tasks : Array<CacheTask> = arrayOf(
        PackSprites(spritesDirectory = settings.getRawCacheLocation("osrs_sprites/")),
        PackSpritesCustom(settings.getRawCacheLocation("sprites/")),
        PackDats(settings.getRawCacheLocation("dats/")),
        PackModels(settings.getRawCacheLocation("models/")),
        PackMaps(settings.getRawCacheLocation("maps/")),
        PackConfig(PackMode.ITEMS,settings.getRawCacheLocation("definitions/items/")),
        PackConfig(PackMode.OBJECTS, settings.getRawCacheLocation("definitions/objects/"))
    )

    when(type) {
        "update" -> {
            val builder = Builder(type = TaskType.FRESH_INSTALL, revision = rev, File(settings.getCacheLocation()))
            builder.extraTasks(*tasks, RemoveXteas(File(settings.getCacheLocation(),"xteas.json"))).build().initialize()
        }
        "build" -> {
            val builder = Builder(type = TaskType.BUILD, revision = rev, File(settings.getCacheLocation()))
            builder.extraTasks(*tasks).build().initialize()
        }
    }
}

