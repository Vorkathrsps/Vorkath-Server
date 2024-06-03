package com.cryptic.clientscripts.impl.journal;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Journal;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class JournalRootInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.JOURNAL_ROOT;
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == 41222147) {
            player.interfaces.handleJournalTab(Journal.CHARACTER_SUMMARY);
        } else if (button == 41222152) {
            player.interfaces.handleJournalTab(Journal.QUEST_TAB);
        } else if (button == 41222157) {
            player.interfaces.handleJournalTab(Journal.ACHIEVEMENT_DIARIES);
        }
    }

}
