package com.cryptic.model.entity.player.save

import com.cryptic.GameConstants
import com.cryptic.model.entity.player.Player
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Jire
 */
object PlayerSaves {

    private const val CAPACITY = GameConstants.PLAYERS_LIMIT * 10

    private val logger: Logger = LoggerFactory.getLogger(PlayerSaves::class.java)

    private val saveEntries: Long2ObjectMap<PlayerSaveRequest> =
        Long2ObjectOpenHashMap(CAPACITY)

    private val insertionQueue: MessagePassingQueue<PlayerSaveRequest> =
        MpscArrayQueue(CAPACITY)

    @JvmStatic
    @JvmOverloads
    fun requestSave(player: Player, whenComplete: Runnable? = null) = insertionQueue.offer(
        PlayerSaveRequest(player, System.currentTimeMillis(), whenComplete))

    @JvmStatic
    fun processSaves() {
        val saveEntries: Long2ObjectMap<PlayerSaveRequest> = saveEntries

        while (!insertionQueue.isEmpty) {
            val request = insertionQueue.poll()
            val (player, _, _) = request

            val key = player.longUsername
            saveEntries.put(key, request)
        }

        if (saveEntries.isNotEmpty()) {
            for ((_, request) in saveEntries.long2ObjectEntrySet()) {
                val (player, _, whenComplete) = request
                val username = player.username

                logger.info("Starting save process for player: {}", username)

                try {
                    PlayerSave.save(player)

                    whenComplete?.run()
                } catch (e: Exception) {
                    logger.error("Error during save process for player: $username", e)
                }

                logger.info("Save process completed for player: {}", username)
            }

            saveEntries.clear()
        }
    }

    @JvmStatic
    fun start() { // u have shutdown hook, doesnt support abrupt (but neither did it  before), true ill throw u 20 extra if you just toggle it in
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(::processSaves, 20, 20, TimeUnit.MILLISECONDS)
    }

}
