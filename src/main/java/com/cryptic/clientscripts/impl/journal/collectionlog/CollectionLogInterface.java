package com.cryptic.clientscripts.impl.journal.collectionlog;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.entity.player.Player;

import java.util.List;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class CollectionLogInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.COLLECTION_LOG;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(List.of(
            new EventNode(48, -1, -1),
            new EventNode(47, -1, -1),
            new EventNode(46, -1, -1),
            new EventNode(45, -1, -1),
            new EventNode(44, -1, -1),
            new EventNode(43, -1, -1),
            new EventNode(42, -1, -1),
            new EventNode(41, -1, -1),
            new EventNode(64, -1, -1),
            new EventNode(63, -1, -1),
            new EventNode(62, -1, -1),
            new EventNode(61, -1, -1),
            new EventNode(60, -1, -1),
            new EventNode(59, -1, -1),
            new EventNode(58, -1, -1),
            new EventNode(57, -1, -1),
            new EventNode(56, -1, -1),
            new EventNode(55, -1, -1),
            new EventNode(54, -1, -1),
            new EventNode(53, -1, -1),
            new EventNode(52, -1, -1),
            new EventNode(51, -1, -1),
            new EventNode(50, -1, -1),
            new EventNode(49, -1, -1),
            new EventNode(69, -1, -1),
            new EventNode(68, -1, -1),
            new EventNode(67, -1, -1),
            new EventNode(66, -1, -1),
            new EventNode(65, -1, -1),
            new EventNode(83, -1, -1),
            new EventNode(82, -1, -1)
        ));
        player.getPacketSender().runClientScriptNew(2388, 0);
        player.getPacketSender().runClientScriptNew(2730, 40697866, 40697867, 40697868, 40697869, 471, 0);
        player.getPacketSender().ifOpenSubModal(gameInterface().getId(), 40, PaneType.FIXED);
        player.varps().setVarbit(Varbits.COLLECTION_LOG_SELECTED_ELEMENT, 0);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        switch (button) {
            case ComponentID.COLLECTION_LOG_CLOSE -> gameInterface().close(player);
            case ComponentID.COLLECTION_LOG_CATEGORY_ONE -> {
                player.varps().setVarp(2048, 0);
                player.varps().setVarp(2941, 1);
                player.varps().setVarbit(Varbits.COLLECTION_LOG_SELECTED_CATEGORY, 1);
                player.getPacketSender().runClientScriptNew(2388, 1, 1);
                player.getPacketSender().runClientScriptNew(2730, 40697870, 40697871, 40697872, 40697878, 472, 0);
            }
            case ComponentID.BEASTIARY_LOOKUP -> {
                return; //TODO
            }
        }
    }
}
