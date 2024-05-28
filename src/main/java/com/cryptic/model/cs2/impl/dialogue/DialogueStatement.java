package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.args.StatementArgs;

public class DialogueStatement extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_STATEMENT;
    }

    @Override
    public void beforeOpen(Player player) {
        var record = player.dialogueRecord.getType();
        if (record instanceof StatementArgs dialogueStatementRecord) {
            player.varps().sendTempVarbit(10670, 0);

            player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_RESULT, joinWithBr(dialogueStatementRecord.messages()));
            player.getPacketSender().setComponentVisability(ComponentID.STATEMENT_CHAT_CONTINUE, !dialogueStatementRecord.continueButtons());
            player.getPacketSender().setComponentText(ComponentID.STATEMENT_CHAT_CONTINUE, "Click here to continue");

            //int lineHeight = getLineHeight(message);
            player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.STATEMENT_CHAT_CONTINUE);

        }
    }

    public static String joinWithBr(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }

        return String.join("<br>", chats);
    }

}
