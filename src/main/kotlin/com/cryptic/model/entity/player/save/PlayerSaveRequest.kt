package com.cryptic.model.entity.player.save

import com.cryptic.model.entity.player.Player

/**
 * @author Jire
 */
data class PlayerSaveRequest(
    val player: Player,
    val timestamp: Long,
    val whenComplete: Runnable?,
)
