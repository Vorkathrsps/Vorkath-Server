package com.cryptic.clientscripts.impl.dialogue.impl;

import com.cryptic.clientscripts.impl.dialogue.information.types.impl.StatementType;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class DialogueStatement extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.DIALOGUE_STATEMENT;
    }

    @Override
    public void beforeOpen(Player player) {
        var dialogueType = player.getDialogueManager().getRecord().getType();
        if (dialogueType instanceof StatementType dialogue) {
            player.varps().sendTempVarbit(10670, 0);
            dialogue.send(player);
        }
    }
}
