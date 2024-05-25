package com.cryptic.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 21, 2020
 */
public class BerryD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendPlayerChat(Expression.NODDING_THREE, "Hello.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            sendNpcChat(NpcIdentifiers.BERRY_7235, Expression.NODDING_THREE, "Good day, adventurer. If you have any questions, I'm", "sure Lars will be more than happy to answer them. He's", "in the building to the south.");
            setPhase(1);
        } else if (getPhase() == 1) {
            sendNpcChat(NpcIdentifiers.BERRY_7235, Expression.NODDING_FIVE, "Also don't forget to visit my sister's axe shop. She works", "in the same building as Lars.");
            setPhase(2);
        } else if(getPhase() == 2) {
            sendPlayerChat(Expression.NODDING_THREE, "Thanks!");
            setPhase(3);
        } else if(getPhase() == 3) {
            stop();
        }
    }

}
