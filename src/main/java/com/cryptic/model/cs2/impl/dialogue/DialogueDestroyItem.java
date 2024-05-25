package com.cryptic.model.cs2.impl.dialogue;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.records.DialogueDestroyItemRecord;

import java.util.ArrayList;
import java.util.List;

public class DialogueDestroyItem extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DESTROY_ITEM;
    }

    @Override
    public void beforeOpen(Player player) {
        DialogueDestroyItemRecord dialogueDestroyItemRecord = player.activeDialogueDestroyItemRecord;
        if (dialogueDestroyItemRecord == null) return;
        setEvents(new EventNode(0, 0, 1, new ArrayList<>(List.of(EventConstants.PAUSE))));
        player.getPacketSender().runClientScriptNew(2379);
        player.getPacketSender().runClientScriptNew(814,
            dialogueDestroyItemRecord.item().getId(),
            dialogueDestroyItemRecord.item().getAmount(),
            0,
            dialogueDestroyItemRecord.title(),
            dialogueDestroyItemRecord.note()
        );
    }


}
