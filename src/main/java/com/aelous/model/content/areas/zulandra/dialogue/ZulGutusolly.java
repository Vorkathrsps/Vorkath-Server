package com.aelous.model.content.areas.zulandra.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 28, 2020
 */
public class ZulGutusolly extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "My brother was chosen as a sacrifice to Zulrah, but", "now the priests say you're going to sacrifice yuorself", "first. You ruined everything!");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "You're angry that I've saved your brother's life?", "I'm sorry.");
            setPhase(1);
        } else if(getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "You don't get it, do you? He'd been looking forward to", "this for ages, and you ruined it. Now we won't even", "get the extra ration of sacred swamp eels.");
            setPhase(3);
        } else if(getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "I hope Zulrah chews you slowly when you sacrifice", "yourself.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "You're angry that I've saved your brother's life?");
                setPhase(2);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "I'm sorry.");
                setPhase(3);
            }
        }
    }
}
