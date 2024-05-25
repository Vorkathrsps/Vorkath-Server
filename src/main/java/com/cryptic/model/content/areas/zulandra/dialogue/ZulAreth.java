package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 27, 2020
 */
public class ZulAreth extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.ZULARETH, Expression.HAPPY, "It's not often that we see more humans in this place.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "How did you come to be here?", "I'm off.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.ZULARETH, Expression.HAPPY, "General Hining sent us out in a scouting party. We", "got separated from the rest, and became completely lost.", "Soon we ran out of rations.");
            setPhase(3);
        } else if(getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.ZULARETH, Expression.HAPPY, "Eventually we stumbled upon this place. They saw we", "were starving, and allowed us to eat of their sacred eels", "if we vowed to serve Zulrah.");
            setPhase(4);
        } else if(getPhase() == 4) {
            sendNpcChat(NpcIdentifiers.ZULARETH, Expression.HAPPY, "Since then, we've settled here. We went back up north", "once, to see how the king's campaign was going, but", "they drove us out of the camp, calling us unclean.");
            setPhase(5);
        } else if(getPhase() == 5) {
            sendNpcChat(NpcIdentifiers.ZULARETH, Expression.HAPPY, "It's probably the sacred eels that did it. They change", "you, you know. Once you've tasted them, you're party", "of the trive forever.");
            setPhase(6);
        } else if(getPhase() == 6) {
            sendPlayerChat(Expression.HAPPY, "Thanks.");
            setPhase(7);
        } else if(getPhase() == 7) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                sendPlayerChat(Expression.HAPPY, "How did you come to be here?");
                setPhase(2);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "I'm off.");
                setPhase(7);
            }
        }
    }
}
