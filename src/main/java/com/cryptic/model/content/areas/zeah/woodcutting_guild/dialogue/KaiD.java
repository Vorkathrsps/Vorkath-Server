package com.cryptic.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin
 * april 21, 2020
 */
public class KaiD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendPlayerChat(Expression.NODDING_FIVE, "Hello! Who are you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (getPhase() == 0) {
            sendNpcChat(NpcIdentifiers.KAI, Expression.NODDING_ONE, "Hi there, my name is Kai and I'm the keeper of this", "here ent habitat.");
            setPhase(1);
        } else if (getPhase() == 1) {
            sendOption(DEFAULT_OPTION_TITLE, "What exactly is an ent?", "That's nice, see you around.");
            setPhase(2);
        } else if (getPhase() == 3) {
            sendNpcChat(NpcIdentifiers.KAI, Expression.SHAKING_HEAD_ONE, "Good question! Ents are ancient beings that resemble", "trees, we believe they draw their energy directly from", "nature.");
            setPhase(4);
        } else if (getPhase() == 4) {
            sendOption(DEFAULT_OPTION_TITLE, "Where did they come from?", "That's nice, see you around.");
            setPhase(5);
        } else if (getPhase() == 6) {
            sendNpcChat(NpcIdentifiers.KAI, Expression.NODDING_FIVE, "Another good question! We're not sure exactly, we just", "know they're a very old race.");
            setPhase(7);
        } else if (getPhase() == 7) {
            sendNpcChat(NpcIdentifiers.KAI, Expression.ANXIOUS, "I have heard of similar creatures roaming a desolate", "part of the eastern continent, though.");
            setPhase(8);
        } else if (getPhase() == 8) {
            sendOption(DEFAULT_OPTION_TITLE, "Interesting... can I fight them?", "Thanks for the information, see you around.");
            setPhase(9);
        } else if (getPhase() == 10) {
            sendNpcChat(NpcIdentifiers.KAI, Expression.SNIGGER, "Of course! There's no shortage of them residing within", "the dungeon, and they're a fantastic source of wood.");
            setPhase(11);
        } else if (getPhase() == 11) {
            sendPlayerChat(Expression.NODDING_THREE, "Thanks!");
            setPhase(12);
        } else if (getPhase() == 12) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(getPhase() == 2) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "What exactly is an ent?");
                setPhase(3);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "That's nice, see you around.");
                setPhase(12);
            }
        } else if(getPhase() == 5) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "Where did they come from?");
                setPhase(6);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "That's nice, see you around.");
                setPhase(12);
            }
        } else if(getPhase() == 9) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "Interesting... can I fight them?");
                setPhase(10);
            } else if(option == 2) {
                sendPlayerChat(Expression.HAPPY, "Thanks for the information, see you around.");
                setPhase(12);
            }
        }
    }
}
