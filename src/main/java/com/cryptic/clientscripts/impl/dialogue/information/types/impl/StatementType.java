package com.cryptic.clientscripts.impl.dialogue.information.types.impl;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.clientscripts.impl.dialogue.information.types.DialogueType;
import com.cryptic.utility.Utils;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public record StatementType(String[] messages, boolean continueButtons) implements DialogueType {
    @Override
    public void send(Player player) {
        player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_RESULT, Utils.joinWithBr(this.messages));
        player.getPacketSender().setComponentVisability(ComponentID.STATEMENT_CHAT_CONTINUE, !this.continueButtons);
        player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_CONTINUE, "Click here to continue");
        player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.STATEMENT_CHAT_CONTINUE);
    }
}
