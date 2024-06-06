package com.cryptic.clientscripts.impl.journal;

import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.interfaces.PaneType;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class CharacterSummaryInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.CHARACTER_SUMMARY;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(3, 3, 7));
        player.getPacketSender().ifOpenSub(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER, true);
    }

}
