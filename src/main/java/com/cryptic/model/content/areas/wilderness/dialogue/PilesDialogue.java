package com.cryptic.model.content.areas.wilderness.dialogue;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.PILES;

/**
 * @author Origin
 * april 07, 2020
 */
public class PilesDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        String name = "BM";
        sendNpcChat(PILES, Expression.CALM_TALK, "Hello. I can convert items to banknotes, for 10 "+name, "per item. Just hand me the items you'd like me to", "convert.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "Who are you?", "Thanks.");
            setPhase(1);
        } else if(getPhase() == 1) {
            sendNpcChat(PILES, Expression.SHAKING_HEAD_ONE, "I'm Piles. I lived in Draynor Village when I was", "young, where I saw three men working in the market,", "converting items to banknotes.");
            setPhase(2);
        } else if(getPhase() == 2) {
            sendNpcChat(PILES, Expression.SHAKING_HEAD_ONE, "Their names were Niles, Miles and Giles. I'm trying to", "be like them, so I've changed my name and started this", "business here.");
            setPhase(3);
        } else if(getPhase() == 3) {
            sendPlayerChat(Expression.SHAKING_HEAD_TWO, "Thanks.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(option == 1) {
            sendPlayerChat(Expression.NODDING_ONE, "Who are you?");
            setPhase(1);
        } else if(option == 2) {
            sendPlayerChat(Expression.SHAKING_HEAD_TWO, "Thanks.");
            setPhase(4);
        }
    }
}
