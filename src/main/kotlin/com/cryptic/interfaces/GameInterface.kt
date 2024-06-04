package com.cryptic.interfaces

import com.cryptic.clientscripts.InterfaceID
import com.cryptic.clientscripts.interfaces.InterfaceHandler
import com.cryptic.model.entity.player.Player


enum class GameInterface(val id: Int, val position: InterfacePosition = InterfacePosition.MAIN_MODAL) {

    //GameFrame

    MINIMAP_ORBS(InterfaceID.MINIMAP, InterfacePosition.MINIMAP_ORBS),
    CHAT(InterfaceID.CHATBOX, InterfacePosition.CHATBOX),

    FIXED_VIEWPORT(InterfaceID.FIXED_VIEWPORT),
    RESIZABLE_VIEWPORT(InterfaceID.RESIZABLE_VIEWPORT),
    RESIZABLE_VIEWPORT_BOTTOM_LINE(InterfaceID.RESIZABLE_VIEWPORT_BOTTOM_LINE),

    //Tab Area
    COMBAT_TAB(InterfaceID.COMBAT, InterfacePosition.COMBAT_TAB),
    SKILL_TAB(InterfaceID.SKILLS, InterfacePosition.SIDE_SKILLS),
    JOURNAL_ROOT(InterfaceID.QUEST_ROOT, InterfacePosition.SIDE_JOURNAL),
    INVENTORY_TAB(InterfaceID.INVENTORY, InterfacePosition.SIDE_INVENTORY),
    EQUIPMENT_TAB(InterfaceID.EQUIPMENT, InterfacePosition.SIDE_EQUIPMENT),
    PRAYER_TAB(InterfaceID.PRAYER, InterfacePosition.SIDE_PRAYER),
    SPELLBOOK_TAB(InterfaceID.SPELLBOOK, InterfacePosition.SIDE_SPELLBOOK),
    SIDE_CHANNELS(InterfaceID.CLAN_CHAT, InterfacePosition.SIDE_CHANNELS),
    FRIEND_LIST_TAB(InterfaceID.FRIEND_LIST, InterfacePosition.SIDE_RELATIONSHIPS),
    IGNORE_LIST_TAB(InterfaceID.IGNORE_LIST, InterfacePosition.SIDE_RELATIONSHIPS),
    ACCOUNT_MANAGEMENT(InterfaceID.ACCOUNT_MANAGEMENT, InterfacePosition.SIDE_ACCOUNT_MANAGEMENT),
    LOGOUT_TAB(InterfaceID.LOGOUT_PANEL, InterfacePosition.SIDE_LOGOUT),
    SETTINGS(InterfaceID.SETTINGS_SIDE, InterfacePosition.SIDE_SETTINGS),
    EMOTE_TAB(InterfaceID.EMOTES, InterfacePosition.SIDE_EMOTES),
    MUSIC_TAB(InterfaceID.MUSIC, InterfacePosition.SIDE_MUSIC),

    //Other
    DIALOGUE_NPC(InterfaceID.DIALOG_NPC,InterfacePosition.DIALOGUE),
    DIALOGUE_PLAYER(InterfaceID.DIALOG_PLAYER,InterfacePosition.DIALOGUE),
    DIALOGUE_OPTIONS(InterfaceID.DIALOG_OPTION,InterfacePosition.DIALOGUE),
    DIALOGUE_STATEMENT(InterfaceID.DIALOG_MESSAGE_BOX,InterfacePosition.DIALOGUE),
    DIALOGUE_ITEM_SINGLE(InterfaceID.DIALOG_SPRITE,InterfacePosition.DIALOGUE),
    DIALOGUE_ITEM_DOUBLE(InterfaceID.DIALOG_DOUBLE_SPRITE,InterfacePosition.DIALOGUE),
    DECANTING(582,InterfacePosition.DIALOGUE),
    DESTROY_ITEM(InterfaceID.DESTROY_ITEM, InterfacePosition.DIALOGUE),
    PRODUCE_ITEM(InterfaceID.PRODUCE_ITEM, InterfacePosition.DIALOGUE),
    CHARACTER_SUMMARY(InterfaceID.CHARACTER_SUMMARY, InterfacePosition.SIDE_JOURNAL),
    ACHIEVEMENT_DIARY(InterfaceID.ACHIEVEMENT_DIARY, InterfacePosition.SIDE_JOURNAL),
    QUEST_LIST(InterfaceID.QUEST_LIST, InterfacePosition.SIDE_JOURNAL),
    EQUIPMENT_INVENTORY(InterfaceID.EQUIPMENT_INVENTORY,InterfacePosition.SINGLE_TAB),
    EQUIPMENT_STATS(InterfaceID.EQUIPMENT_STATS),
    EXPERIENCE_TRACKER(InterfaceID.EXPERIENCE_TRACKER,InterfacePosition.XP_TRACKER),
    EXPERIENCE_TRACKER_SETUP(InterfaceID.XP_DROPS),
    QUICK_PRAYERS(InterfaceID.QUICK_PRAYER, InterfacePosition.SIDE_PRAYER),
    SKOTIZO_OVERLAY(InterfaceID.SKOTIZO,InterfacePosition.OVERLAY),
    WORLD_MAP(InterfaceID.WORLD_MAP,InterfacePosition.WORLD_MAP),

    PRIVATE_CHAT(InterfaceID.PRIVATE_CHAT,InterfacePosition.PRIVATE_CHAT),

    PVP_OVERLAY(InterfaceID.PVP,InterfacePosition.PVP_OVERLAY),
    SKILL_INFORMATION(InterfaceID.SKILL_INFORMATION, InterfacePosition.SKILL_INFORMATION),

    FIXED_PANE(548),
    RESIZABLE_PANE(161),
    MOBILE_PANE(601),
    SIDE_PANELS_RESIZABLE_PANE(164),

    ;

    companion object {
        val VALUES = values()

        fun get(id: Int): GameInterface? {
            val field = VALUES.find { it.id == id }
            return field
        }
    }

    fun open(player: Player) = InterfaceHandler.find(this.id)?.open(player) ?: player.interfaces.sendInterface(this)

    fun close(player: Player) = player.interfaces.closeInterface(this.position)

}
