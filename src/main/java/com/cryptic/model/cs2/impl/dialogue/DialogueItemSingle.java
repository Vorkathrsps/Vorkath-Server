package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.DialogueSingleItemRecord;

import java.util.ArrayList;
import java.util.List;

public class DialogueItemSingle extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_ITEM_SINGLE;
    }

    @Override
    public void beforeOpen(Player player) {
        DialogueSingleItemRecord dialogueItemSingle = player.activeSingleItemRecord;
        if (dialogueItemSingle == null) return;
        setEvents(new EventNode(0, 0, 1, new ArrayList<>(List.of(EventConstants.PAUSE))));

        player.getPacketSender().setItemMessage(ComponentID.DIALOG_SPRITE_SPRITE,dialogueItemSingle.item().getId(),dialogueItemSingle.item().getAmount());
        player.getPacketSender().setComponentText(ComponentID.DIALOG_SPRITE_TEXT,  joinWithBr(dialogueItemSingle.messages()));

        player.getPacketSender().runClientScriptNew(2868, dialogueItemSingle.continueButton() ? "Click here to continue" : "");
    }

    public static String joinWithBr(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }

        return String.join("<br>", chats);
    }

}
