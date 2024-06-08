package com.cryptic.interfaces

import com.cryptic.clientscripts.constants.InterfaceID
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

enum class InterfacePosition(val resizableComponent: Int, val gameframeInterfaceId: Int = -1, val type: InterfaceType = InterfaceType.OVERLAY) {

    CHATBOX(96, InterfaceID.CHATBOX),
    MINIMAP_ORBS(33, InterfaceID.MINIMAP),
    MAIN_MODAL(16, InterfaceType.MODAL),
    IN_VIEWPORT(548, InterfaceType.MODAL),
    WORLD_MAP(18, type = InterfaceType.OVERLAY),
    DIALOGUE(565, InterfaceType.MODAL),
    WILDERNESS_OVERLAY(4),
    SINGLE_TAB(74, InterfaceType.MODAL),
    XP_TRACKER(9),
    OVERLAY(1),
    PVP_OVERLAY(3),
    COMBAT_TAB(76, InterfaceID.COMBAT),
    SIDE_SKILLS(77, InterfaceID.SKILLS),
    SIDE_JOURNAL(78, InterfaceID.QUEST_ROOT),
    SIDE_INVENTORY(79, InterfaceID.INVENTORY),
    SIDE_EQUIPMENT(80, InterfaceID.EQUIPMENT),
    SIDE_PRAYER(81, InterfaceID.PRAYER),
    SIDE_SPELLBOOK(82, InterfaceID.SPELLBOOK),
    SIDE_CHANNELS(83, InterfaceID.CLAN_CHAT),
    SIDE_RELATIONSHIPS(85, InterfaceID.FRIEND_LIST),
    SIDE_ACCOUNT_MANAGEMENT(84, InterfaceID.ACCOUNT_MANAGEMENT),
    SIDE_LOGOUT(86, InterfaceID.LOGOUT_PANEL),
    SIDE_SETTINGS(87, InterfaceID.SETTINGS_SIDE),
    SIDE_EMOTES(88, InterfaceID.EMOTES),
    SIDE_MUSIC(89, InterfaceID.MUSIC),
    PRIVATE_CHAT(93, InterfaceID.PRIVATE_CHAT),

    MINIGAME_OVERLAY(6), //TODO
    UNKNOWN_OVERLAY(8), //TODO
    MULTI(94);

    companion object {
        /**
         * An array containing all of the component types.
         */
        val VALUES = values()

        /**
         * Gets the component pairs when moving from one game pane to another.
         *
         * @param fromPane the pane we're moving from.
         * @param toPane the pane we're moving to.
         * @return a primitive int map containing all the pairs.
         */
        fun getPairs(fromPane: PaneType, toPane: PaneType): Int2IntOpenHashMap {
            val pairs = Int2IntOpenHashMap(VALUES.size)
            for (position in VALUES) {
                if (position == DIALOGUE) {
                    continue
                }
                val from = position.getComponent(fromPane)
                val to = position.getComponent(toPane)
                if (from != -1 && to != -1) {
                    pairs[from] = to
                }
            }
            return pairs
        }

        /**
         * Gets the component type for the respective component id and pane.
         *
         * @param componentId the resizable component id.
         * @param pane the pane to search.
         * @return the respective component type, or null if not found.
         */
        fun getPosition(componentId: Int, pane: PaneType?): InterfacePosition? {
            for (position in VALUES) {
                val e = pane?.getEnum()
                val bitpacked = e?.getInt(161 shl 16 or componentId) ?: 0
                if (componentId == (bitpacked and 0xFFFF)) {
                    return position
                }
            }
            return null
        }
    }

    /**
     * Constructs the component types with the seed component of resizable, used to search the other types.
     *
     * @param resizableComponent the resizable component id, used as a seed.
     * @param type whether the component is walkable or not.
     */
    constructor(resizableComponent: Int) : this(resizableComponent, -1, InterfaceType.OVERLAY)

    /**
     * Constructs the component types with the seed component of resizable, used to search the other types.
     *
     * @param resizableComponent the resizable component id, used as a seed.
     * @param type whether the component is walkable or not.
     */
    constructor(resizableComponent: Int, type: InterfaceType) : this(resizableComponent, -1, type)

    /**
     * Gets the component id for the respective pane based on the resizable type.
     *
     * @param pane the pane to seek.
     * @return the component id
     */
    fun getComponent(pane: PaneType?): Int {
        if (pane == null) {
            return -1
        } else if (pane == PaneType.RESIZABLE) {
            return resizableComponent
        }
        val e = pane.getEnum()
        val bitpacked = e.getInt(161 shl 16 or resizableComponent)
        return if (bitpacked == 0) -1 else bitpacked and 0xFFFF
    }

    /**
     * Gets the fixed component based on the resizable one.
     *
     * @return the fixed component's id.
     */
    fun getFixedComponent(): Int {
        val e = PaneType.FIXED.getEnum()
        val bitpacked = e.getInt(161 shl 16 or resizableComponent)
        return if (bitpacked == 0) -1 else bitpacked and 0xFFFF
    }

    /**
     * Gets the fullscreen component based on the resizable one.
     *
     * @return the fullscreen component's id.
     */
    fun getFullScreenComponent(): Int {
        val e = PaneType.FULL_SCREEN.getEnum()
        val bitpacked = e.getInt(161 shl 16 or resizableComponent)
        return if (bitpacked == 0) -1 else bitpacked and 0xFFFF
    }

    /**
     * Gets the mobile component based on the resizable one.
     *
     * @return the mobile component's id.
     */
    fun getMobileComponent(): Int {
        val e = PaneType.MOBILE.getEnum()
        val bitpacked = e.getInt(161 shl 16 or resizableComponent)
        return if (bitpacked == 0) -1 else bitpacked and 0xFFFF
    }

    /**
     * Gets the sidepanels component based on the resizable one.
     *
     * @return the sidepanels component's id.
     */
    fun getSidepanelsComponent(): Int {
        val e = PaneType.SIDE_PANELS.getEnum()
        val bitpacked = e.getInt(161 shl 16 or resizableComponent)
        return if (bitpacked == 0) -1 else bitpacked and 0xFFFF
    }
}
