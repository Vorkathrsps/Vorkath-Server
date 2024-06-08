package com.cryptic.clientscripts.impl.journal;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.InterfaceType;
import com.cryptic.interfaces.Journal;
import com.cryptic.interfaces.PaneType;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class AchievementTabInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.ACHIEVEMENT_DIARY;
    }

    @Override
    public boolean sendInterface() {
        return false;
    }

    @Override
    public void beforeOpen(Player player) {
        setEvents(new EventNode(2, 0, 11));
        player.getPacketSender().ifOpenSubWalkable(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER);
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }

}
