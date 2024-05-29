package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 28, 2020
 */
public class ZulCheray extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.ZULCHERAY, Expression.DEFAULT, "You queue-jumper! My eldest son was chosen as able", "sacrifice to Zulrah, but now the priests say you're going", "to sacrifice yourself first.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "You're angry that I've saved your brother's life?", "I'm sorry.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.ZULCHERAY, Expression.DEFAULT, "The sacrifice is the greatest honour to which any of us", "can aspire! He was so excited to be coming face-to-face", "with Zulrah, and offering himself on behalf of the tribe.");
            setPhase(3);
        } else if(getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.ZULCHERAY, Expression.DEFAULT, "Also, I won't get the extra large ration of sacred swamp", "eels if my son doesn't make the sacrifice. Shame on", "you!");
            setPhase(4);
        } else if(getPhase() == 4) {
            sendPlayerChat(Expression.DEFAULT, "I see.");
            setPhase(5);
        } else if(getPhase() == 5) {
            sendNpcChat(NpcIdentifiers.ZULCHERAY, Expression.DEFAULT, "I hope Zulrah chews you slowly when you sacrifice", "yourself.");
            setPhase(6);
        } else if(getPhase() == 6) {
            stop();
        } else if(getPhase() == 7) {
            sendNpcChat(NpcIdentifiers.ZULCHERAY, Expression.DEFAULT, "Quite right. Now get on with sacrificing yourself.");
            setPhase(6);
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
                setPhase(7);
            }
        }
    }
}
