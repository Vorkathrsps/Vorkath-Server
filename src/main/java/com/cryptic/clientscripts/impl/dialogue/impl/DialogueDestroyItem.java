package com.cryptic.clientscripts.impl.dialogue.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.constants.EventConstants;
import com.cryptic.clientscripts.util.EventNode;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.clientscripts.impl.dialogue.information.types.impl.DestroyItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class DialogueDestroyItem extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DESTROY_ITEM;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof DestroyItemType dialogue) {
            setEvents(new EventNode(0, 0, 1, new ArrayList<>(List.of(EventConstants.PAUSE))));
            dialogue.send(player);
        }
    }

    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
