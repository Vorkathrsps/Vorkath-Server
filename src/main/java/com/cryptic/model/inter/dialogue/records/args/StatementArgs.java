package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public record StatementArgs(String[] messages, boolean continueButtons) implements Arguements {
    @Override
    public void send(Player player) {
        player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_RESULT, Utils.joinWithBr(this.messages));
        player.getPacketSender().setComponentVisability(ComponentID.STATEMENT_CHAT_CONTINUE, !this.continueButtons);
        player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_CONTINUE, "Click here to continue");
        player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.STATEMENT_CHAT_CONTINUE);
    }
}
