package com.aelous.model.content.security;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.utility.Color;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SECURITY_GUARD;
import static com.aelous.model.entity.attributes.AttributeKey.ACCOUNT_PIN;
import static com.aelous.model.entity.attributes.AttributeKey.ASK_FOR_ACCOUNT_PIN;

/**
 * @author Patrick van Elderen | April, 29, 2021, 18:18
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SecurityAdvisorDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Set Account PIN", "Change Account PIN", "Remove Account PIN", "Nevermind");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(1)) {
            stop();
        }
        if (isPhase(2)) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(0)) {
            if (option == 1) {
                var pin = player.<Integer>getAttribOr(AttributeKey.ACCOUNT_PIN, 0);
                String pinToString = Integer.toString(pin);
                if (pinToString.length() == 5) {
                    send(DialogueType.NPC_STATEMENT, SECURITY_GUARD, Expression.HAPPY, "You already have an account pin.");
                    setPhase(2);
                    return;
                }
                send(DialogueType.PLAYER_STATEMENT, Expression.ANNOYED, "I would like to setup an Account PIN.");
                setPhase(1);
            }
            if (option == 2) {
                stop();
                var pin = player.<Integer>getAttribOr(AttributeKey.ACCOUNT_PIN, 0);
                String pinToString = Integer.toString(pin);
                if (pinToString.length() != 5) {
                    send(DialogueType.NPC_STATEMENT, SECURITY_GUARD, Expression.HAPPY, "You have to setup an account pin first.");
                    setPhase(2);
                    return;
                }
            }
            if (option == 3) {
                var pin = player.<Integer>getAttribOr(AttributeKey.ACCOUNT_PIN, 0);
                String pinToString = Integer.toString(pin);
                if (pinToString.length() != 5) {
                    send(DialogueType.NPC_STATEMENT, SECURITY_GUARD, Expression.HAPPY, "You have to setup an account pin first.");
                    setPhase(2);
                    return;
                }
                stop();
                player.setAmountScript("Confirm your pin in order to remove it.", new InputScript() {
                    @Override
                    public boolean handle(Object value) {
                        int pin = (Integer) value;
                        String pinToString = Integer.toString(pin);
                        if(pinToString.equalsIgnoreCase(Integer.toString(player.<Integer>getAttribOr(ACCOUNT_PIN,0)))) {
                            player.putAttrib(ACCOUNT_PIN,0);
                            player.putAttrib(ASK_FOR_ACCOUNT_PIN,false);
                            player.message(Color.GREEN.wrap("Your account pin has been removed."));
                        } else {
                            DialogueManager.npcChat(player, Expression.ANNOYED, SECURITY_GUARD,"Your account pin did not match!");
                        }
                        return true;
                    }
                });
            }
            if (option == 4) {
                stop();
            }
        }
    }
}
