package com.cryptic.model.content.bank_pin.dialogue;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class DeleteBankPinDialogue extends Dialogue {
    private final int npcId;

    public DeleteBankPinDialogue(int npcId) {
        this.npcId = npcId;
    }


    @Override
    protected void start(Object... parameters) {
        sendNpcChat(npcId, Expression.DEFAULT, "Are you absolutely sure you wish to delete your bank pin?");
        setPhase(0);
    }

    @Override
    protected void next() {
        switch (getPhase()) {
            case 0:
                sendOption("Select an option.",
                    "Yes",
                    "No");
                break;
        }
    }

    @Override
    protected void select(int option) {
        switch (getPhase()) {
            case 0:
                if (option == 1) {
                    player.getBankPin().deletePin();
                }
                stop();
                break;
        }
    }
}
