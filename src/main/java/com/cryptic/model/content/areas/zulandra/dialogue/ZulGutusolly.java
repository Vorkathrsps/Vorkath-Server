package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 28, 2020
 */
public class ZulGutusolly extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "My brother was chosen as a sacrifice to Zulrah, but", "now the priests say you're going to sacrifice yuorself", "first. You ruined everything!");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "You're angry that I've saved your brother's life?", "I'm sorry.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "You don't get it, do you? He'd been looking forward to", "this for ages, and you ruined it. Now we won't even", "get the extra ration of sacred swamp eels.");
            setPhase(3);
        } else if(getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.ZULGUTUSOLLY, Expression.DEFAULT, "I hope Zulrah chews you slowly when you sacrifice", "yourself.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if (option == 1) {
                sendPlayerChat(Expression.DEFAULT, "You're angry that I've saved your brother's life?");
                setPhase(2);
            } else if (option == 2) {
                sendPlayerChat(Expression.DEFAULT, "I'm sorry.");
                setPhase(3);
            }
        }
    }
}
