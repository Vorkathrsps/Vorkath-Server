package com.cryptic.clientscripts.impl.journal;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.interfaces.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.QuestTabUtils;

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
        long gameTime = player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L);
        setEvents(new EventNode(3, 3, 7));
        player.getPacketSender().ifOpenSubWalkable(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER);
        player.getPacketSender().runClientScriptNew(3970, 46661634, 46661635, gameTime / 100L);
        player.getPacketSender().runClientScriptNew(ScriptID.CHARACTER_SUMMARY_COMBAT_LEVEL, 46661634, 46661635, player.skills().combatLevel());
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.COLLECTION_LOG_BUTTON) {
            switch (slot) {
                case 6 -> GameInterface.COLLECTION_LOG.open(player);
                case 7 -> {
                    long gameTime = player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L);
                    player.varps().toggleVarbit(12933);
                    player.getPacketSender().runClientScriptNew(3970, 46661634, 46661635, gameTime / 100L);
                }
            }
        }
    }

}
