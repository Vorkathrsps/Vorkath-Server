package com.cryptic.model.cs2.impl.journal;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
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
        player.getPacketSender().sendInterface(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER, true);
    }

}
