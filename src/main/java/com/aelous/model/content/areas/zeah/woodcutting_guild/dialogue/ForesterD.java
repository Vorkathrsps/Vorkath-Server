package com.aelous.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 21, 2020
 */
public class ForesterD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int roll = Utils.random(2);

        if(roll == 1) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.FORESTER_7238, Expression.NODDING_THREE, "Nice weather we're having today.");
        } else {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.FORESTER_7238, Expression.HAPPY, "It's so peaceful here, don't you agree?");
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
