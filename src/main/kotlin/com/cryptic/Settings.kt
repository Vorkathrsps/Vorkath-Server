package com.cryptic

import com.cryptic.model.map.position.Tile
import java.io.File
import cc.ekblad.toml.decode
import cc.ekblad.toml.tomlMapper

data class ServerSettings(
    val name : String = "Valor",
    val homePos : List<Int> = listOf(3097, 3501, 0),
    val dataLocation : String = "./data/"
) {

    val homeTile : Tile = Tile(homePos[0], homePos[1], homePos[2])

    fun getCacheLocation() = File(dataLocation,"cache").path
    fun getRawCacheLocation(dir : String) = File(dataLocation,"raw-cache/$dir/")

    fun getRawCacheLocation() = File(dataLocation,"cache").path

}
object ServerSettingsManager {


    private val mapper = tomlMapper {

    }

    val SAVE_LOCATION = File("./settings.toml")

    var settings : ServerSettings = ServerSettings()

    fun init() {
        if(SAVE_LOCATION.exists()) {
            settings = mapper.decode<ServerSettings>(SAVE_LOCATION.toPath())
        } else {
            println(mapper.encode(settings).toString())
        }
    }

}
