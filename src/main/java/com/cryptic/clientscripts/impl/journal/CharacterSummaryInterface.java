package com.cryptic.clientscripts.impl.journal;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.interfaces.Varbits;
import com.cryptic.model.entity.attributes.AttributeKey;
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
        long gameTime = player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L);
        setEvents(new EventNode(3, 3, 7));
        player.getPacketSender().ifOpenSubWalkable(gameInterface().getId(), 28, PaneType.JOURNAL_TAB_HEADER);
        player.getPacketSender().runClientScriptNew(ScriptID.TIME_PLAYED, ComponentID.CHARACTER_SUMMARY_CONTAINER, ComponentID.COLLECTION_LOG, gameTime / 100L);
        player.getPacketSender().runClientScriptNew(ScriptID.CHARACTER_SUMMARY_COMBAT_LEVEL, ComponentID.CHARACTER_SUMMARY_CONTAINER, ComponentID.COLLECTION_LOG, player.skills().combatLevel());
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        if (button == ComponentID.COLLECTION_LOG) {
            switch (slot) {
                case 6 -> GameInterface.COLLECTION_LOG.open(player);
                case 7 -> {
                    long gameTime = player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L);
                    player.varps().toggleVarbit(Varbits.TOGGLE_TIME_PLAYED);
                    player.getPacketSender().runClientScriptNew(ScriptID.TIME_PLAYED, ComponentID.CHARACTER_SUMMARY_CONTAINER, ComponentID.COLLECTION_LOG, gameTime / 100L);
                }
            }
        }
    }

}
