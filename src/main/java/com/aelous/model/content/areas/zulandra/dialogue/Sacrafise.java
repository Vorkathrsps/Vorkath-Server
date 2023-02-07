package com.aelous.model.content.areas.zulandra.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 27, 2020
 */
public class Sacrafise extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.SACRIFICE, Expression.HAPPY, "The High Priestess says you're to be the sacrifice", "instead of me. I was so looking forward to meeting", "Zulrah.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Don't you understand that I've saved your life?", "I'm sorry.");
            setPhase(1);
        } else if(getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.SACRIFICE, Expression.CALM_TALK, "In death, the nutrients of my lowly body were destined", "to nourish the mighty Zulrah. I would have become one", "with my god.");
            setPhase(3);
        } else if(getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.SACRIFICE, Expression.HAPPY, "Still, it is nice to live a little longer. Thank you for what", "you have done.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        } else if(getPhase() == 5) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.SACRIFICE, Expression.SAD, "While I am saddened that my destiny has not been", "fullfilled, it is nice to live a little longer. Thank you forward", "what you have done.");
            setPhase(4);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Don't you understand that I've saved your life?");
                setPhase(2);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'm sorry.");
                setPhase(5);
            }
        }
    }
}
