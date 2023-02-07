package com.aelous.model.content.bank_pin.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.npc.NPC;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class BankTellerDialogue extends Dialogue {
    private int npcId;

    @Override
    protected void start(Object... parameters) {
        NPC npc = (NPC) parameters[0];
        npcId = npc.id();
        player.setPositionToFace(npc.tile());
        npc.setPositionToFace(player.tile());
        send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT, "Good day, how may I help you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION,
                "Select an option.",
                "I'd like to access my bank account, please.",
                "I'd like to check my PIN settings.");
            setPhase(1);
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(1)) {
            if (option == 1) {
                player.getBank().open();
            } else if (option == 2) {
                player.getBankPinSettings().open(npcId);
            }
        }
    }
}
