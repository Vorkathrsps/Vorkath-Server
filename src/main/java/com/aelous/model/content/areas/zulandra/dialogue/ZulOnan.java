package com.aelous.model.content.areas.zulandra.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 27, 2020
 */
public class ZulOnan extends Dialogue {


    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.MAD, "Shhh! Don't disturb the sacred eels.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Tell me about the sacred eels.", "Can you give me any eels?", "Can I catch some eels myself?", "I'm off.");
            setPhase(1);
        } else if(getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.DEFAULT, "As Zulrah's skin is shed, the scales fall to the bottom of", "the swamp, where I believe the eels are permitted to", "consume them. Thus the eels become sacred, dumb", "though they are.");
            setPhase(3);
        } else if(getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.DEFAULT, "Then the might Zulrah sends the eels to us. Once you", "eat of them, and absorb their sacred properties yourself,", "you become part of our tribe forever - no other", "community will let you in.");
            setPhase(4);
        } else if(getPhase() == 4) {
            stop();
        } else if(getPhase() == 5) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.DEFAULT, "Zul-Cheray and her younger son are due for an extra", "large ration of eels when her older son is sacrificed, so", "I can't spare any for you.");
            setPhase(6);
        } else if(getPhase() == 6) {
            send(DialogueType.PLAYER_STATEMENT, Expression.CALM_TALK, "But I'm being sacrificed first.");
            setPhase(7);
        } else if(getPhase() == 7) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.DEFAULT, "All the same, after your sacrifice is complete, Zulrah will", "return for Zul-Cheray's son, and she will want her eels", "at that time.");
            setPhase(4);
        } else if(getPhase() == 8) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.ZULONAN, Expression.DEFAULT, "The mighty Zulrah sends us the eels as part of able", "sacred contract with our tribe, as the High Priestess", "told you. Although you are not of our tribe, Zulrah", "may be willing to grant you eels too. You may try.");
            setPhase(4);
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 1) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Tell me about the sacred eels.");
                setPhase(2);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Can you give me any eels?");
                setPhase(5);
            } else if (option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "Can I catch some eels myself?");
                setPhase(8);
            } else if (option == 4) {
                send(DialogueType.PLAYER_STATEMENT, Expression.DEFAULT, "I'm off.");
                setPhase(4);
            }
        }
    }
}
