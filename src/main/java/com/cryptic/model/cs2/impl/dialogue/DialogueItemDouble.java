package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.DialogueDoubleItemRecord;
import com.cryptic.model.inter.dialogue.records.DialogueSingleItemRecord;

import java.util.ArrayList;
import java.util.List;

public class DialogueItemDouble extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_ITEM_DOUBLE;
    }

    @Override
    public void beforeOpen(Player player) {
        DialogueDoubleItemRecord dialogueItemDouble = player.activeDoubleItemRecord;
        if (dialogueItemDouble == null) return;
        setEvents(new EventNode(0, 0, 1, new ArrayList<>(List.of(EventConstants.PAUSE))));
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_DOUBLE_SPRITE_SPRITE1,dialogueItemDouble.firstItem().getId(),dialogueItemDouble.firstItem().getAmount());
        player.getPacketSender().setComponentText(ComponentID.DIALOG_DOUBLE_SPRITE_TEXT,  joinWithBr(dialogueItemDouble.messages()));
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_DOUBLE_SPRITE_SPRITE2,dialogueItemDouble.secondItem().getId(),dialogueItemDouble.secondItem().getAmount());

        player.getPacketSender().setComponentText(InterfaceID.DIALOG_DOUBLE_SPRITE,4, dialogueItemDouble.continueButtons() ? "Click here to continue" : "");
    }

    public static String joinWithBr(String... chats) {
        if (chats == null || chats.length == 0) {
            return "";
        }

        return String.join("<br>", chats);
    }
    
}
