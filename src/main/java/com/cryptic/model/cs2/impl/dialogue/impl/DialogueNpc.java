package com.cryptic.model.cs2.impl.dialogue.impl;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.cs2.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.cs2.impl.dialogue.information.types.impl.NpcType;

public class DialogueNpc extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_NPC;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof NpcType dialogue) {
            player.varps().sendTempVarbit(10670, 0);
            dialogue.send(player);
        }
    }
}
