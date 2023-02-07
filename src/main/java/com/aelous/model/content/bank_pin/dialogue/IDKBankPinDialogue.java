package com.aelous.model.content.bank_pin.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class IDKBankPinDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        if (player.getBankPin().hasPin()) {
            setPhase(0);
            send(DialogueType.STATEMENT, "If you do not know your PIN, it will have to be deleted.",
                "Are you okay with this?");
        } else {
            setPhase(1);
            send(DialogueType.STATEMENT, "You do not have a bank pin yet.");
        }

    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            send(DialogueType.OPTION, "Select an option.",
                "Yes",
                "No");
        } else if (getPhase() == 1) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if (getPhase() == 0) {
            if (option == 1) {
                player.getBankPin().deletePin();
                stop();
            } else if (option == 2) {
                stop();
            }
        }
    }
}
