package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 27, 2020
 */
public class ZulUrgish extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULURGISH, Expression.HAPPY, "Human agreed to sacrifice himself to might Zulrah!", "We all most grateful.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            stop();
        }
    }
}
