package com.aelous.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 21, 2020
 */
public class BerryD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_THREE, "Hello.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.BERRY_7235, Expression.NODDING_THREE, "Good day, adventurer. If you have any questions, I'm", "sure Lars will be more than happy to answer them. He's", "in the building to the south.");
            setPhase(1);
        } else if (getPhase() == 1) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.BERRY_7235, Expression.NODDING_FIVE, "Also don't forget to visit my sister's axe shop. She works", "in the same building as Lars.");
            setPhase(2);
        } else if(getPhase() == 2) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_THREE, "Thanks!");
            setPhase(3);
        } else if(getPhase() == 3) {
            stop();
        }
    }

}
