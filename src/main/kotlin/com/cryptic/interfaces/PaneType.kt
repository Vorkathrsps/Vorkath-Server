package com.cryptic.interfaces

import com.cryptic.GameServer
import dev.openrune.cache.CacheManager
import dev.openrune.cache.filestore.definition.data.EnumType

enum class PaneType(val id: Int, val enumId: Int) {

    FIXED(548, 1129),
    RESIZABLE(161, 1130),
    SIDE_PANELS(164, 1131),
    FULL_SCREEN(165, 1132),
    ORB_OF_OCULUS(16, -1),
    GAME_SCREEN(80, -1),
    CHATBOX(162, -1),
    JOURNAL_TAB_HEADER(629, -1),
    MOBILE(601, 1745);

    fun getEnum(): EnumType {
        if (enumId == -1) {
            throw RuntimeException("No enum exists for the pane: $this.")
        }
        return GameServer.getCacheManager().getEnum(enumId)
    }
}
