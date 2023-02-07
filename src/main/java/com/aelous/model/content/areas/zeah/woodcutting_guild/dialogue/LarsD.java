package com.aelous.model.content.areas.zeah.woodcutting_guild.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 21, 2020
 */
public class LarsD extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.NODDING_FIVE, "Welcome to the woodcutting guild. How may I help", "you?");
        setPhase(0);
    }

    @Override
    protected void next() {
       // System.out.println("[next] Current phase: "+getPhase());
        if (getPhase() == 0) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Tell me about the guild.", "Tell me about yourself.", "I'm just passing by.");
            setPhase(1);
        } else if (getPhase() == 2) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.SHAKING_HEAD_THREE, "I founded the woodcutting guild when I retired from", "the Shayzien military. I spotted the ancient redwood", "trees in the hills many years ago from the Shayzien", "camp, and decided to construct the guild around them.");
            setPhase(3);
        } else if (getPhase() == 3) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.NODDING_THREE, "You can find them to the West, just follow the path.");
            setPhase(4);
        } else if (getPhase() == 4) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.DEFAULT, "However, if you're skilled in combat, or simply afraid of", "heights, the ent cavern may be more suited for you.", "The ents dwell deep beneath the ancient redwoods, and", "can be accessed through the cave to the North-West.");
            setPhase(5);
        } else if (getPhase() == 5) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.NODDING_ONE, "Is there anything else I can help you with?");
            setPhase(6);
        } else if (getPhase() == 6) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Tell me about yourself.", "I'm just passing by.");
            setPhase(7);
        } else if (getPhase() == 8) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.SHAKING_HEAD_ONE, "My name is Lars, and I am the founder of the", "woodcutting guild. I originally grew up in a Shayzien", "camp, and was raised on the battlefield.");
            setPhase(9);
        } else if (getPhase() == 9) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.SHAKING_HEAD_THREE, "As I grew older, I found peace in the Hosidius house,", "and the joys of skilled labour. I turned in my battleaxe", "for a hatchet, and set up a guild for those also talented", "in the skill of woodcutting.");
            setPhase(10);
        } else if (getPhase() == 10) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.NODDING_ONE, "Is there anything else I can help you with?");
            setPhase(11);
        } else if (getPhase() == 11) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Tell me about the guild.", "That's all, thanks.");
            setPhase(12);
        } else if (getPhase() == 13) {
            stop();
        } else if (getPhase() == 14) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'm just passing by.");
            setPhase(15);
        } else if (getPhase() == 15) {
            send(DialogueType.NPC_STATEMENT, NpcIdentifiers.GUILDMASTER_LARS, Expression.NODDING_THREE, "Sure, let me know if there's anything I can do for you.");
            setPhase(13);
        }
    }

    @Override
    protected void select(int option) {
       // System.out.println("[select] Current phase: "+getPhase());
        if (getPhase() == 1) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Tell me about the guild.");
                setPhase(2);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Tell me about yourself.");
                setPhase(8);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'm just passing by.");
                setPhase(15);
            }
        } else if (getPhase() == 7) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Tell me about yourself.");
                setPhase(8);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I'm just passing by.");
                setPhase(15);
            }
        } else if (getPhase() == 12) {
            if(option == 1) {
                setPhase(1);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "That's all, thanks.");
                setPhase(13);
            }
        }
    }
}
