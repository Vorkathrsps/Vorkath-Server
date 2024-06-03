package com.cryptic.model.content.bank_pin.dialogue;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class IDKBankPinDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        if (player.getBankPin().hasPin()) {
            setPhase(0);
            sendStatement("If you do not know your PIN, it will have to be deleted.",
                "Are you okay with this?");
        } else {
            setPhase(1);
            sendStatement("You do not have a bank pin yet.");
        }

    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            sendOption("Select an option.",
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
