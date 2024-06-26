package com.cryptic.model.entity.player.save

import com.cryptic.model.entity.attributes.AttribType
import com.cryptic.model.entity.attributes.AttributeKey
import com.cryptic.model.entity.player.Player
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Type
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author Jak Shadowrs tardisfan121@gmail.com
 */
object PSaveSerializers {

    val log: Logger = LoggerFactory.getLogger("PSaveSerializers")

    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val mapStringStringType: Type = object : TypeToken<Map<String?, String?>?>() {
    }.type

    private fun jsonToMapStringString(json: String?): Map<String, String> {
        try {
            if (json == null || json.isEmpty()) return HashMap()
            return gson.fromJson(json, mapStringStringType)
        } catch (e: Exception) {
            return HashMap() //Ultimate backwards compatibility
        }
    }

    private fun mapStringStringToJson(map: Map<String?, String?>): String {
        var map: Map<String?, String?>? = map
        if (map == null) map = HashMap()
        return gson.toJson(map)
    }

    @JvmStatic
    fun init() {
        PlayerSave.ARGS_DESERIALIZER =
            BiConsumer<Player, Map<String, String>> { player: Player?, stringStringMap: Map<String, String>? ->
                if (stringStringMap == null || player == null) return@BiConsumer
                stringStringMap.forEach(BiConsumer<String, String> { k: String, v: String ->
                    for (key in Arrays.stream<AttributeKey>(AttributeKey.values())
                        .filter { ak: AttributeKey -> ak.saveName() != null && ak.saveType() != null }
                        .collect(Collectors.toSet<AttributeKey>())) {
                        if (key.saveName() == null || key.saveName() != k) {
                            continue
                        }
                        try {
                            if (key.saveType() == AttribType.INTEGER) {
                                player.putAttrib(key, v.toInt())
                            } else if (key.saveType() == AttribType.STRING) {
                                player.putAttrib(key, v)
                            } else if (key.saveType() == AttribType.DOUBLE) {
                                player.putAttrib(key, v.toDouble())
                            } else if (key.saveType() == AttribType.LONG) {
                                player.putAttrib(key, v.toLong())
                            } else if (key.saveType() == AttribType.BOOLEAN) {
                                player.putAttrib(key, v.toBoolean())
                            } else if (key.saveType() == AttribType.ARRAY) {
                                log.error("BAD SERIZLIZE UNSUPPORT TYPE: ARRAY - $key")
                            } else if (key.saveType() == AttribType.STRING_STRING_MAP) {
                                player.putAttrib(key, jsonToMapStringString(v))
                            }
                            //log.info("succesfully parsed {} {} {}", k, v, key.saveType());
                        } catch (e: Exception) {
                            log.error("error loading profile- Attribute key $key : ", e)
                        }
                    }
                })
            }
        PlayerSave.ARGS_SERIALIZER = Function<Player, Map<String, String>> { player: Player ->
            val attribs = HashMap<String, String>()
            for (key in AttributeKey.values()) {
                if (!player.hasAttrib(key)) {
                    continue
                }
                if (key.saveType() == null) continue
                try {
                    when (key.saveType()) {
                        AttribType.INTEGER -> {
                            val v = player.getAttribOr<Int>(key, 0)
                            attribs.put(key.saveName(), v.toString())
                        }

                        AttribType.STRING -> {
                            val v = player.getAttribOr<String>(key, null)
                            if (v != null) {
                                attribs.put(key.saveName(), v)
                            }
                        }

                        AttribType.DOUBLE -> {
                            val v = player.getAttribOr<Double>(key, 0.0)
                            if (v != null) {
                                attribs.put(key.saveName(), v.toString())
                            }
                        }

                        AttribType.LONG -> {
                            val value = player.getAttribOr<Long>(key, 0L)
                            attribs.put(key.saveName(), value.toString())
                        }

                        AttribType.BOOLEAN -> {
                            val state = player.getAttribOr<Boolean>(key, false)
                            attribs.put(key.saveName(), state.toString())
                        }

                        AttribType.ARRAY -> {
                            log.error("unused save type ARRAY key $key")
                        }

                        AttribType.STRING_STRING_MAP -> {
                            val v = player.getAttribOr<Map<String?, String?>>(key, false)
                            if (v != null) {
                                attribs.put(key.saveName(), mapStringStringToJson(v).toString())
                            }
                        }
                    }
                } catch (e: Exception) {
                    log.error("error loading profile ATTRIBUTE KEY $key: ", e)
                }
            }
            attribs
        }
    }
}
