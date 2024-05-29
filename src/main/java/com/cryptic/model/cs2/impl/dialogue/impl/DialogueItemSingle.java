package com.cryptic.model.cs2.impl.dialogue.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.interfaces.EventConstants;
import com.cryptic.model.cs2.interfaces.EventNode;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.cs2.impl.dialogue.information.types.impl.SingleItemType;

import java.util.ArrayList;
import java.util.List;

public class DialogueItemSingle extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_ITEM_SINGLE;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof SingleItemType dialogue) {
            setEvents(new EventNode(0, 0, 1, new ArrayList<>(List.of(EventConstants.PAUSE))));
            dialogue.send(player);
        }
    }
}
