package com.cryptic.model.content.bank_pin.dialogue;

import com.cryptic.model.content.bank_pin.BankPin;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;

/**
 * @author lare96 <http://github.com/lare96>
 */
public class IncorrectBankPinDialogue extends Dialogue {

    private final BankPin bankPin;
    private final Runnable action;

    public IncorrectBankPinDialogue(BankPin bankPin, Runnable action) {
        this.bankPin = bankPin;
        this.action = action;
    }

    @Override
    protected void start(Object... parameters) {
        setPhase(0);
        sendStatement("<col=ca0d0d>That number was incorrect.", "Please try again.");
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            action.run();
        }
    }
}
