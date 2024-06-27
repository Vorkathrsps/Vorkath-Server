package com.cryptic.tools

import com.cryptic.GameServer
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
import dev.openrune.cache.tools.tasks.impl.defs.json.PackItems
import java.io.File

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
    val type = args.first()
    val rev = 221

    val tasks : Array<CacheTask> = arrayOf(
        PackSprites(spritesDirectory = getRawCacheLocation("osrs_sprites/")),
        PackSpritesCustom(getRawCacheLocation("sprites/")),
        PackDats(getRawCacheLocation("dats/")),
        PackModels(getRawCacheLocation("models/")),
        PackMaps(getRawCacheLocation("maps/")),
        //PackItems(settings.getRawCacheLocation("definitions/items/"))//Old json way but its not gonna be supported long just convert them takes like 10 mins..
        //PackConfig(PackMode.OBJECTS,settings.getRawCacheLocation("definitions/objects/")),
        PackConfig(PackMode.ITEMS,getRawCacheLocation("definitions/items/")),
        PackConfig(PackMode.OBJECTS, getRawCacheLocation("definitions/objects/"))
    )

    when(type) {
        "update" -> {
            val builder = Builder(type = TaskType.FRESH_INSTALL, revision = rev, File(getCacheLocation()))
            builder.extraTasks(*tasks, RemoveXteas(File(getCacheLocation(),"xteas.json"))).build().initialize()
        }
        "build" -> {
            val builder = Builder(type = TaskType.BUILD, revision = rev, File(getCacheLocation()))
            builder.extraTasks(*tasks).build().initialize()
        }
    }
}

