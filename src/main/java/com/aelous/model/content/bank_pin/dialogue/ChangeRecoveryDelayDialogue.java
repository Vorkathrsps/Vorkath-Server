package com.aelous.model.content.bank_pin.dialogue;

import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class ChangeRecoveryDelayDialogue extends Dialogue {

    private final int npcId;

    public ChangeRecoveryDelayDialogue(int npcId) {
        this.npcId = npcId;
    }

    @Override
    protected void start(Object... parameters) {
        sendMainMenu();
    }

    @Override
    protected void next() {
        switch (getPhase()) {
            case 0 -> player.setAmountScript("Enter your new recovery delay.", value -> {
                int input = (Integer) value;
                if (input < 3) {
                    sendInvalid("shorter than 3 days");
                } else if (input > 30) {
                    sendInvalid("longer than 30 days");
                } else {
                    player.getBankPin().changeRecoveryDays(input);
                    stop();
                }
                return true;
            });
            case 1 -> sendMainMenu();
        }
    }

    private void sendInvalid(String type) {
        send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT, "The recovery delay cannot be " + type + ".");
        setPhase(1);
    }

    private void sendMainMenu() {
        send(DialogueType.NPC_STATEMENT, npcId, Expression.DEFAULT, "How long would you like the recovery delay to be?");
        setPhase(0);
    }
}
