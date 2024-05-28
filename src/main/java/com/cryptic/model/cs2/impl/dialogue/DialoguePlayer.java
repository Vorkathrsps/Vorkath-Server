package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.args.PlayerArgs;

public class DialoguePlayer extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_PLAYER;
    }

    @Override
    public void beforeOpen(Player player) {
        var record = player.dialogueRecord.getType();
        if (record instanceof PlayerArgs dialoguePlayerRecord) {

            String dialogTitle = !dialoguePlayerRecord.title().isBlank() ? dialoguePlayerRecord.title() : player.getUsername();
            String message = joinWithBr(dialoguePlayerRecord.chats());
            player.varps().sendTempVarbit(10670, 0);

            player.getPacketSender().setPlayerHeadMesssage(ComponentID.PLAYER_CHAT_HEAD);
            player.getPacketSender().setAnimMessage(ComponentID.PLAYER_CHAT_HEAD, dialoguePlayerRecord.expression().getAnimation().getId());

            player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_TITLE, dialogTitle);
            player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_MESSAGE, message);

            player.getPacketSender().setComponentVisability(ComponentID.PLAYER_CHAT_CONTINUE, !dialoguePlayerRecord.continueButtons());
            player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_CONTINUE, "Click here to continue");

            //int lineHeight = getLineHeight(message);
            player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.PLAYER_CHAT_MESSAGE);
        }
    }

    public static String joinWithBr(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }

        return String.join("<br>", chats);
    }


}
