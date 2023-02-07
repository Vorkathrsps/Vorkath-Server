package com.aelous.model.content.areas.zulandra.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 27, 2020
 */
public class ZulAreth extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULARETH, Expression.HAPPY, "It's not often that we see more humans in this place.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "How did you come to be here?", "I'm off.");
            setPhase(1);
        } else if(getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULARETH, Expression.HAPPY, "General Hining sent us out in a scouting party. We", "got separated from the rest, and became completely lost.", "Soon we ran out of rations.");
            setPhase(3);
        } else if(getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULARETH, Expression.HAPPY, "Eventually we stumbled upon this place. They saw we", "were starving, and allowed us to eat of their sacred eels", "if we vowed to serve Zulrah.");
            setPhase(4);
        } else if(getPhase() == 4) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULARETH, Expression.HAPPY, "Since then, we've settled here. We went back up north", "once, to see how the king's campaign was going, but", "they drove us out of the camp, calling us unclean.");
            setPhase(5);
        } else if(getPhase() == 5) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULARETH, Expression.HAPPY, "It's probably the sacred eels that did it. They change", "you, you know. Once you've tasted them, you're party", "of the trive forever.");
            setPhase(6);
        } else if(getPhase() == 6) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Thanks.");
            setPhase(7);
        } else if(getPhase() == 7) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "How did you come to be here?");
                setPhase(2);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'm off.");
                setPhase(7);
            }
        }
    }
}
