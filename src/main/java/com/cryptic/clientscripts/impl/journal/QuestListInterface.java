package com.cryptic.clientscripts.impl.journal;

import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class QuestListInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.QUEST_LIST;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }


    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(3, 3, 7));
        player.getPacketSender().ifOpenSubWalkable(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

}
