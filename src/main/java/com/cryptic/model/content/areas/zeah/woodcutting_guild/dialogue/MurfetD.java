package com.cryptic.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 21, 2020
 */
public class MurfetD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendPlayerChat(Expression.NODDING_THREE, "Hello.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            sendNpcChat(NpcIdentifiers.MURFET, Expression.NODDING_ONE, "Hello, how can I help you?");
            setPhase(1);
        } else if (getPhase() == 1) {
            sendOption(DEFAULT_OPTION_TITLE, "What's in the cave?", "I was just passing through.");
            setPhase(2);
        } else if (getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.MURFET, Expression.CALM_TALK, "Creatures known as ents inhabit the depths of the hills,", "rumour has it they reside there due to the presence of", "the ancient redwood trees on the hills.");
            setPhase(4);
        } else if (getPhase() == 4) {
            sendNpcChat(NpcIdentifiers.MURFET, Expression.CALM_TALK, "If you're as skilled a fighter as you are a woodcutter,", "you may enter the caves. You should find Kai once", "you are inside, he can give you more information.");
            setPhase(5);
        } else if (getPhase() == 5) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if (getPhase() == 2) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "What's in the cave?");
                setPhase(3);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "I was just passing through.");
                setPhase(5);
            }
        }
    }
}
