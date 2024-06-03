package com.cryptic.clientscripts.impl.dialogue.impl;

import com.cryptic.clientscripts.impl.dialogue.information.types.impl.PlayerType;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

public class DialoguePlayer extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_PLAYER;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof PlayerType dialogue) {
            player.varps().sendTempVarbit(10670, 0);
            dialogue.send(player);
            player.getPacketSender().runClientScriptNew(600, 1, 1, 16, ComponentID.PLAYER_CHAT_MESSAGE);
        }
    }
}
