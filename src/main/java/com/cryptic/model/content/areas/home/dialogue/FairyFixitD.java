package com.cryptic.model.content.areas.home.dialogue;

import com.cryptic.model.World;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.FAIRY_FIXIT_7333;

/**
 * @author Origin | April, 23, 2021, 13:53
 * 
 */
public class FairyFixitD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, FAIRY_FIXIT_7333, Expression.DEFAULT, "Pssst! Human! I've got something for you.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "What have you got for me?", "Not interested, thanks.");
            setPhase(1);
        } else if(isPhase(2)) {
            send(DialogueType.NPC_STATEMENT, FAIRY_FIXIT_7333, Expression.DEFAULT, "I've got imbuement scrolls which might help if you're", "working with rings.");
            setPhase(3);
        } else if(isPhase(3)) {
            stop();
            World.getWorld().shop(42).open(player);
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT,Expression.DEFAULT, "What have you got for me?");
                setPhase(2);
            }
            if(option == 2) {
                stop();
            }
        }
    }
}
