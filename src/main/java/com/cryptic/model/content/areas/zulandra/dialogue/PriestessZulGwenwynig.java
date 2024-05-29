package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 27, 2020
 */
public class PriestessZulGwenwynig extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "You left the shrine. Did Zulrah reject your sacrifice?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "I killed Zulrah.", "Remind me how this ritual works.", "I'm off.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "Is that what you think? Board the boat again, and I am", "sure you will find Zulrah has overcome whatever pitiful", "attacks you used against him.");
            setPhase(7);
        } else if(getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "Once I have brought you to the shrine, I will leave you", "there as an offering to Zulrah. You cannot use the boat", "to return.");
            setPhase(4);
        } else if(getPhase() == 4) {
            sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "You can use your magical powers to flee from Zulrah's", "grasp, though that would defeat the point of the sacrifice,", "and you'll have to try again.");
            setPhase(5);
        } else if(getPhase() == 5) {
            sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "When Zulrah inevitably kills you, I may be able to", "retrieve your dropped possessions. If so, I will hold them", "here.");
            setPhase(6);
        } else if(getPhase() == 6) {
            sendNpcChat(NpcIdentifiers.PRIESTESS_ZULGWENWYNIG_2033, Expression.DEFAULT, "Now board the boat and prepare to be sacrificed.");
            setPhase(7);
        } else if(getPhase() == 7) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                sendPlayerChat(Expression.HAPPY, "I killed Zulrah.");
                setPhase(2);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "Remind me how this ritual works.");
                setPhase(3);
            } else if(option == 3) {
                sendPlayerChat(Expression.HAPPY, "I'm off.");
                setPhase(7);
            }
        }
    }
}
