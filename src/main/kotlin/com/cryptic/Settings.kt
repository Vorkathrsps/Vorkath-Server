package com.cryptic

import com.cryptic.model.map.position.Tile
import java.io.File
import cc.ekblad.toml.decode
import cc.ekblad.toml.tomlMapper

fun getCacheLocation() = File("./data/","cache").path
fun getRawCacheLocation(dir : String) = File("./data/","raw-cache/$dir/")

fun getRawCacheLocation() = File("./data/","cache").path
