package com.aelous.model.content.bank_pin.dialogue;

import com.aelous.model.content.bank_pin.BankPinModification;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class BankPinSettingsDialogue extends Dialogue {

    private final int npcId;
    private BankPinModification pinMod;

    public BankPinSettingsDialogue(int npcId) {
        this.npcId = npcId;
    }

    @Override
    protected void start(Object... parameters) {
        player.getBankPin().activateMod();
        pinMod = player.getBankPin().getPendingMod();
        if (pinMod != null) {
            long hours = LocalDateTime.now().until(pinMod.getActivationDate(), ChronoUnit.HOURS);
            String remainingStr = hours > 0 ? "<col=ca0d0d>(" + hours + "h left)" : "(under an hour left)";
            send(DialogueType.OPTION,
                "Select an option.",
                "Cancel '" + pinMod.getDescription() + "' " + remainingStr,
                "Nevermind");
            setPhase(4);
        } else {
            if (player.getBankPin().hasPin()) {
                send(DialogueType.OPTION, "Select an option.",
                    "Change your pin",
                    "Delete your pin",
                    "Change your recovery delay",
                    "Nevermind");
                setPhase(0);
            } else {
                send(DialogueType.OPTION, "Select an option.",
                    "Create a pin",
                    "Nevermind");
                setPhase(1);
            }
        }
    }

    @Override
    protected void next() {
        switch (getPhase()) {
            case 0:
            case 1:
                send(DialogueType.OPTION, "Select an option.",
                    "4 digits",
                    "5 digits",
                    "6 digits",
                    "7 digits",
                    "8 digits");
                if (isPhase(0)) {
                    setPhase(3);
                } else if (isPhase(1)) {
                    setPhase(2);
                }
                break;
            case 5:
                send(DialogueType.OPTION,
                    "Select an option.",
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
                    send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT, "How long would you like your new pin to be?");
                } else if (option == 2) {
                    player.getBankPinSettings().deletePin(npcId);
                } else if (option == 3) {
                    player.getBankPinSettings().changeRecoveryDelay(npcId);
                } else if (option == 4) {
                    stop();
                }
                break;
            case 1:
                if (option == 1) {
                    send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT,
                        "How long would you like your pin to be?");
                } else if (option == 2) {
                    stop();
                }
                break;
            case 2:
            case 3:
                int digits = -1;
                if (option == 1) {
                    digits = 4;
                } else if (option == 2) {
                    digits = 5;
                } else if (option == 3) {
                    digits = 6;
                } else if (option == 4) {
                    digits = 7;
                } else if (option == 5) {
                    digits = 8;
                }
                player.getBankPinSettings().createPin(digits);
                break;
            case 4:
                if (option == 1) {
                    send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT,
                        "Are you sure you would like to cancel '" + pinMod.getDescription() + "'?");
                    setPhase(5);
                } else if (option == 2) {
                    stop();
                }
                break;
            case 5:
                if (option == 1) {
                    player.message("The pending change '" + pinMod.getDescription() + "' has been cancelled.");
                    player.getBankPin().setPendingMod(null);
                }
                stop();
                break;
        }
    }
}
