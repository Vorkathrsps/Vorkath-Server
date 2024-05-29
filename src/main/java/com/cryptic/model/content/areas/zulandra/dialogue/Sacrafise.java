package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 27, 2020
 */
public class Sacrafise extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.SACRIFICE, Expression.HAPPY, "The High Priestess says you're to be the sacrifice", "instead of me. I was so looking forward to meeting", "Zulrah.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "Don't you understand that I've saved your life?", "I'm sorry.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.SACRIFICE, Expression.CALM_TALK, "In death, the nutrients of my lowly body were destined", "to nourish the mighty Zulrah. I would have become one", "with my god.");
            setPhase(3);
        } else if(getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.SACRIFICE, Expression.HAPPY, "Still, it is nice to live a little longer. Thank you for what", "you have done.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        } else if(getPhase() == 5) {
            sendNpcChat(NpcIdentifiers.SACRIFICE, Expression.SAD, "While I am saddened that my destiny has not been", "fullfilled, it is nice to live a little longer. Thank you forward", "what you have done.");
            setPhase(4);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                sendPlayerChat(Expression.HAPPY, "Don't you understand that I've saved your life?");
                setPhase(2);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "I'm sorry.");
                setPhase(5);
            }
        }
    }
}
