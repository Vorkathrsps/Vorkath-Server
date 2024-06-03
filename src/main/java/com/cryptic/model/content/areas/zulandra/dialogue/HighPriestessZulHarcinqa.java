package com.cryptic.model.content.areas.zulandra.dialogue;

import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 27, 2020
 */
public class HighPriestessZulHarcinqa extends Dialogue {


    @Override
    protected void start(Object... parameters) {
        sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "You offered yourself to Zulrah as a sacrifice, yet here", "you stand before me. Did Zulrah reject you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            sendOption(DEFAULT_OPTION_TITLE, "I killed Zulrah.", "Have you any advice for surviving against Zulrah?", "Tell me about Zulrah.", "Who are you all?", "I'm off.");
            setPhase(1);
        } else if(getPhase() == 2) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Very funny humad, but I find that hard to believe. Go", "and offer yourself as a sacrifice again, before the", "mighty Zulrah grows impatient.");
            setPhase(3);
        } else if(getPhase() == 3) {
            stop();
        } else if(getPhase() == 4) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "I think you have misunderstoord the nature of the", "sacrifice, human. Never mind, the might Zulrah will", "make the matter clear to you.");
            setPhase(3);
        } else if(getPhase() == 5) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Zulrah is the all-powerful snake god of the southern", "wastes. He rises from the swamp to show his divine", "majesty and to accept our homage.");
            setPhase(6);
        } else if(getPhase() == 6) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Long ago, our forebears came to this place, not realising", "that they were trespassing in Zulrah's domain. He arose", "from the swamp in wrath, and many were slain.");
            setPhase(7);
        } else if(getPhase() == 7) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "But Zulrah, in his mercy, allowed some of those", "transgressors to live, sparing them the punishment they", "deserved, and allowing them the chance to repent.");
            setPhase(8);
        } else if(getPhase() == 8) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Then my mother, oldest and wisest of the tribe, dared", "to intercede with the mighty Zulrah on behalf of here", "people, and thus a sacred contract was declared.");
            setPhase(9);
        } else if(getPhase() == 9) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Zulrah granted us the right to dwell in his lands, and to", "eat of their bounty. None shall ever drive us out, for", "Zulrah protects us.");
            setPhase(10);
        } else if(getPhase() ==10) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "All he asks in return is a periodic sacrifice at this sacred", "shrine. As long as the sacrifices continue, Zulrah will let", "us live peavefully in his lands.");
            setPhase(11);
        } else if(getPhase() == 11) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Willing volunteers are never hard to find, for their", "sacrifice buys peace for the tribe, and I grant their", "families extra rations of food.");
            setPhase(3);
        } else if(getPhase() == 12) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "The soutern wastes are a perilous place where", "travellers often become lost. Elves from the woodland", "villages, gnomes from the eastern caves, and many", "others.");
            setPhase(13);
        } else if(getPhase() == 13) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Some find their way here, like my forebears did. They", "eat of the sacred swamp eels that Zulrah sends, and", "become one with the tribe.");
            setPhase(14);
        } else if(getPhase() == 14) {
            sendNpcChat(NpcIdentifiers.HIGH_PRIESTESS_ZULHARCINQA, Expression.DEFAULT, "Their original families and tribes shun them as unclean", "creatures once they tasted of the sacred swamp", "eels, so they remain here to worship Zulrah.");
            setPhase(3);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if(option == 1) {
                sendPlayerChat(Expression.HAPPY, "I killed Zulrah.");
                setPhase(2);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "Have you any advice for surviving against Zulrah?");
                setPhase(4);
            } else if(option == 3) {
                sendPlayerChat(Expression.HAPPY, "Tell me about Zulrah.");
                setPhase(5);
            } else if(option == 4) {
                sendPlayerChat(Expression.HAPPY, "Who are you all?");
                setPhase(12);
            } else if(option == 5) {
                sendPlayerChat(Expression.HAPPY, "I'm off.");
                setPhase(3);
            }
        }
    }
}
