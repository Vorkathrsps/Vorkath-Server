package com.cryptic.interfaces

import com.cryptic.model.cs2.impl.weaponinterface.WeaponInformationInterface
import com.cryptic.model.entity.player.Player
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableMap

class InterfaceSystem(private val player: Player) {

    var visible: BiMap<Int, Int> = HashBiMap.create()

    private var pane: PaneType? = null

    private var journal: Journal = Journal.QUEST_TAB

    init {
        pane = PaneType.FIXED
        visible[pane!!.id shl 16] = pane!!.id
    }


    fun sendPane(fromPane: PaneType, toPane: PaneType) {
        player.packetSender.sendPane(toPane)
        visible.remove(fromPane.id shl 16)
        visible.forcePut(pane!!.id shl 16, toPane.id)
        val pairs = InterfacePosition.getPairs(fromPane, toPane)
        pairs.forEach { (k, v) -> moveInterface(fromPane, k, toPane, v) }
    }

    private fun isSidePanels(): Boolean {
        return player.varps().getVarp(Varbits.SIDESTONES_ARRAGEMENT_VARBIT) == 1
    }

    private fun isResizable(): Boolean {
        return player.displayMode.isResizable()
    }

    fun setInterfaceUnderlay(color: Int, transparency: Int) {
        player.packetSender.runClientScriptNew(917, color, transparency)
        player.packetSender.runClientScriptNew(2524, color, transparency)
    }

    fun openInterfaceForMode(mode : DisplayMode) {
        when(mode) {
            DisplayMode.FIXED -> GameInterface.FIXED_VIEWPORT.open(player)
            DisplayMode.RESIZABLE_NORMAL -> TODO()
            DisplayMode.RESIZABLE_LIST -> TODO()
            DisplayMode.MOBILE -> TODO()
            DisplayMode.FULLSCREEN -> TODO()
        }
    }

    fun closeInterfaceForMode(mode : DisplayMode) {
        when(mode) {
            DisplayMode.FIXED -> GameInterface.FIXED_VIEWPORT.close(player)
            DisplayMode.RESIZABLE_NORMAL -> TODO()
            DisplayMode.RESIZABLE_LIST -> TODO()
            DisplayMode.MOBILE -> TODO()
            DisplayMode.FULLSCREEN -> TODO()
        }
    }


    fun sendGameFrame() {
        val pane = if (player.device == Device.MOBILE) PaneType.MOBILE
        else if (isResizable()) if (isSidePanels()) PaneType.SIDE_PANELS else PaneType.RESIZABLE
        else PaneType.FIXED

        openInterfaceForMode(player.displayMode)

        player.packetSender.sendPane(pane)

        InterfacePosition.VALUES.forEach { position ->
            if (position.gameframeInterfaceId == -1 || position == InterfacePosition.SIDE_RELATIONSHIPS || position == InterfacePosition.SIDE_JOURNAL) {
                return@forEach
            }

            val gameInter = GameInterface.get(position.gameframeInterfaceId)
            gameInter!!.open(player)
        }
        sendMisc()
        handleJournalTab()
        handleJournalTab()
        handleRelationShipTab()
        player.varps().setVarbit(Varbits.CHATBOX_UNLOCKED,1)
    }

    fun handleRelationShipTab(toggleTab: Boolean = false) {

        if (toggleTab) {
            player.varps().toggleVarbit(Varbits.FRIEND_FACE_ID_VARBIT)
        }

        val isFriendTab = player.varps().getVarbit(Varbits.FRIEND_FACE_ID_VARBIT) == 0
        val interfaceToOpen = if (isFriendTab) GameInterface.FRIEND_LIST_TAB else GameInterface.IGNORE_LIST_TAB

        interfaceToOpen.open(player)
    }


    fun handleJournalTab(newTab: Journal = journal) {
        GameInterface.JOURNAL_ROOT.open(player)
        if (newTab != journal) {
            journal = newTab
        }
        player.varps().setVarbit(Varbits.JOURNAL_TAB_INTERFACE,newTab.ordinal)
        when (journal) {
            Journal.CHARACTER_SUMMARY -> GameInterface.CHARACTER_SUMMARY.open(player)
            Journal.QUEST_TAB -> GameInterface.QUEST_LIST.open(player)
            Journal.ACHIEVEMENT_DIARIES -> GameInterface.ACHIEVEMENT_DIARY.open(player)
        }
    }

    fun toggleDisplayInterface(mode: DisplayMode) {
        if (player.displayMode != mode) {
            closeInterfaceForMode(player.displayMode)
            player.displayMode = mode
            openInterfaceForMode(mode)
            sendGameFrame()
        }
    }

    private fun sendMisc() {
        WeaponInformationInterface.updateWeaponInfo(player)
    }

    fun getInterfaceComponent(interfaceId: Int): Int {
        return visible.inverse().getOrDefault(interfaceId, -1) and 0xFFFF
    }

    fun getInterfacePane(interfaceId: Int): Int {
        return visible.inverse().getOrDefault(interfaceId, -1) shr 16
    }

    fun getInterface(pane: PaneType, paneComponent: Int): Int {
        return visible.getOrDefault(pane.id shl 16 or paneComponent, -1)
    }

    fun containsInterface(position: InterfacePosition): Boolean {
        return when {
            position == InterfacePosition.CHATBOX || position == InterfacePosition.DIALOGUE ->
                visible.containsKey(PaneType.CHATBOX.id shl 16 or InterfacePosition.DIALOGUE.getComponent(PaneType.RESIZABLE))

            else ->
                visible.containsKey(PaneType.FIXED.id shl 16 or position.getFixedComponent()) ||
                        visible.containsKey(PaneType.SIDE_PANELS.id shl 16 or position.getSidepanelsComponent()) ||
                        visible.containsKey(PaneType.RESIZABLE.id shl 16 or position.resizableComponent) ||
                        visible.containsKey(PaneType.MOBILE.id shl 16 or position.getMobileComponent())
        }
    }

    fun sendInterface(interfaceId: Int, paneComponent: Int, pane: PaneType, walkable: InterfaceType) {
        player.packetSender.sendInterfaceOSRS(interfaceId, paneComponent, pane, walkable)
        visible.forcePut(pane.id shl 16 or paneComponent, interfaceId)
    }

    fun sendInterface(gameInterface: GameInterface) {
        sendInterface(gameInterface.position, gameInterface.id)
    }


    fun sendInterface(position: InterfacePosition, id: Int) {
        sendInterface(position, id, position.type)
    }

    private val BACKGROUND_SCRIPT_ARGS: Map<Int, Array<Int>> = ImmutableMap.builder<Int, Array<Int>>()
        .put(25, arrayOf(5066031, 125)).put(52, arrayOf(3612928, 0))
        .put(57, arrayOf(4535323, 0)).put(398, arrayOf(4212288, 50))
        .put(224, arrayOf(4404769, 0)).put(116, arrayOf(4404769, 0))
        .put(267, arrayOf(65792, 0)).put(299, arrayOf(2760198, 0))
        .put(134, arrayOf(0, 195)).build()

    private val EXPANDED_INTERFACES = intArrayOf(12, 139, 400, 345, 310, 700, 704, 709)

    private val EXPANDED_ARGS = arrayOf<Any>(-1, -2)
    private val DEFAULT_ARGS = arrayOf<Any>(-1, -1)

    fun sendInterface(position: InterfacePosition, id: Int, type: InterfaceType) {

        if (position == InterfacePosition.DIALOGUE) {
            closeInterface(InterfacePosition.MAIN_MODAL)
        } else if (position == InterfacePosition.MAIN_MODAL) {
            closeInterface(InterfacePosition.DIALOGUE)
        }
        val pane =
            if (position == InterfacePosition.DIALOGUE) PaneType.CHATBOX else this.pane
        val paneToObtainComponentFrom = if (pane == PaneType.CHATBOX) PaneType.RESIZABLE else pane
        if (position == InterfacePosition.MAIN_MODAL) {
            player.packetSender.runClientScriptNew(2524, if (EXPANDED_INTERFACES.contains(id)) EXPANDED_ARGS else DEFAULT_ARGS)
            if (BACKGROUND_SCRIPT_ARGS.containsKey(id)) {
                player.packetSender.runClientScriptNew(917, BACKGROUND_SCRIPT_ARGS[id]!!)
            }
        }

        player.packetSender.sendInterfaceOSRS(id, position.getComponent(paneToObtainComponentFrom), pane, type)
    }

    fun closeInterface(position: InterfacePosition, removeFromMap: Boolean, closeEvent: Boolean) {
        val contains = containsInterface(position)
        val dialogue = position == InterfacePosition.DIALOGUE
        val pane = if (dialogue) PaneType.RESIZABLE else this.pane
        val hash = (if (dialogue) PaneType.CHATBOX.id else pane?.id ?: 0) shl 16 or (position?.getComponent(pane) ?: 0)
        //closeInput()
        var previous: Int? = null
        if (removeFromMap) {
            previous = visible.remove(hash)
        }
        if (contains) {
            //player.closeInterface(hash)
            //if (dialogue) {
               // val dial = player.dialogueManager.lastDialogue
                //if (dial != null) {
                  //  dial.npc?.finishInteractingWith(player)
                //}
            //}
        }
        if (contains && previous != null) {
           //// val prevGameInterface = GameInterface.get(previous)
           // prevGameInterface.ifPresent { gameInterface -> closePlugin(player, gameInterface, Optional.empty()) }
        }
        if (closeEvent && contains && position == InterfacePosition.MAIN_MODAL) {
           // val runnable = player.closeInterfacesEvent
            //if (runnable == null) {
              //  return
           // }
           // runnable.run()
           // player.closeInterfacesEvent = null
        }
    }

    fun closeInterface(position: InterfacePosition) {
        closeInterface(position, true, true)
    }

    fun closeInterface(gameInterface: GameInterface) {
        closeInterface(gameInterface.position)
    }

    fun closeInterface(interfaceId: Int) {
        val componentId = getInterfaceComponent(interfaceId)
        val position = InterfacePosition.getPosition(componentId, pane)
        if (position == null) {
            return
        }
        closeInterface(position, true, true)
    }

    fun closeInterfaces() {
        closeInterface(InterfacePosition.MAIN_MODAL)
        closeInterface(InterfacePosition.SINGLE_TAB)
        closeInterface(InterfacePosition.DIALOGUE)
    }

    fun isPresent(inter: GameInterface): Boolean {
        return isVisible(inter.id)
    }

    private fun moveInterface(fromPane: PaneType, fromComponent: Int, toPane: PaneType, toComponent: Int) {
        if (isVisible(214)) {
            closeInterface(InterfacePosition.MAIN_MODAL)
        }

        val interfaceId = getInterface(fromPane, fromComponent)
        player.packetSender.sendMoveInterface(fromPane.id, fromComponent, toPane.id, toComponent)
        visible.remove(fromPane.id shl 16 or fromComponent)
        visible.forcePut(toPane.id shl 16 or toComponent, interfaceId)
    }

    fun isVisible(id: Int): Boolean {
        return pane?.id == id || visible.inverse().containsKey(id)
    }

    fun isVisible(pane: Int, paneComponent: Int): Boolean {
        return visible.containsKey(pane shl 16 or paneComponent)
    }

    fun setPane(pane: PaneType) {
        this.pane = pane
        visible.forcePut(-1, pane.id)
    }

    fun setJournal(journal: Journal) {
        this.journal = journal
        player.varps().setVarbit(8168, journal.ordinal)
        when (journal) {
            Journal.CHARACTER_SUMMARY -> GameInterface.CHARACTER_SUMMARY.open(player)
            Journal.QUEST_TAB -> GameInterface.JOURNAL_ROOT.open(player)
            Journal.ACHIEVEMENT_DIARIES -> GameInterface.ACHIEVEMENT_DIARY.open(player)
        }
    }

}
