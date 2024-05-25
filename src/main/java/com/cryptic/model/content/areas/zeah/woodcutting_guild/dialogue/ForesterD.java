package com.cryptic.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.Utils;

/**
 * @author Origin
 * april 21, 2020
 */
public class ForesterD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int roll = Utils.random(2);

        if(roll == 1) {
            sendNpcChat(NpcIdentifiers.FORESTER_7238, Expression.NODDING_THREE, "Nice weather we're having today.");
        } else {
            sendNpcChat(NpcIdentifiers.FORESTER_7238, Expression.HAPPY, "It's so peaceful here, don't you agree?");
        }
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            stop();
        }
    }

}
