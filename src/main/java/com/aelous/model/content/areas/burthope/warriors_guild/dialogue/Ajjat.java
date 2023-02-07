package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.AJJAT;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Ajjat extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, AJJAT, Expression.CALM_TALK, "Greetings, fellow warrior. I am Ajjat, former black", "knight and now training officer here in the warrior", "guild.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Can you tell me about skillcapes, please?", "Black Knight? Why are you here?", "What's the Dummy Room all about?", "May I claim my tokens please?", "Bye!");
            setPhase(1);
        } else if(isPhase(2)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.CALM_TALK, "Skillcapes, also known as Capes of Accomplishment, are", "reserved for the elite of the elite. Only a person who", "has truly mastered a skill can buy one, and even then", "a skillcape can only be bought from one who is");
            setPhase(3);
        } else if(isPhase(3)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.DEFAULT, "recognised as the highest skilled in the land at any", "particular skill. I have the privilege of being the person", "that controls access to the Skillcape of Attack.");
            setPhase(4);
        } else if(isPhase(4)) {
            stop();
        } else if(isPhase(5)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.DEFAULT, "Indeed I was, however their... methods... did not match", "with my ideals.. so I left. Harrallak, recognising my", "talent as a warrior, took me in and offered me a job", "here.");
            setPhase(6);
        } else if(isPhase(6)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Hmm... well if Harrallak trusts you, I guess I can.");
            setPhase(4);
        } else if(isPhase(7)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.SHAKING_HEAD_THREE, "Ahh yes, the dummies. Another ingenious invention of", "the noble Dwarf Gamfred. They're mechanical you see", "and pop up out of the floor. You have to hit them with", "the correct attack mode before they disappear again.");
            setPhase(8);
        } else if(isPhase(8)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "So how do I tell which one is which?");
            setPhase(9);
        } else if(isPhase(9)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.SHAKING_HEAD_THREE, "There are two different ways. One indication is their", "colour, the other is the pose and weapons they are", "holding, for instance, the one holding daggers you will", "need to hit with a piercing attack.");
            setPhase(10);
        } else if(isPhase(10)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.NODDING_FIVE, "In the room you will find a poster on the wall which", "can help you recognise each different dummy.");
            setPhase(11);
        } else if(isPhase(11)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "That sounds ingenious!");
            setPhase(12);
        } else if(isPhase(12)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.SHAKING_HEAD_ONE, "Indeed, you may find that you need several weapons to", "be successful 100% of the time, but keep trying. The", "weapons shop upstairs may help you there.");
            setPhase(4);
        } else if(isPhase(13)) {
            //it.chatNpc("Of course! Here you go, you've earned 30 tokens!", Ajjat, 569)
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.VERY_SAD, "I'm afraid you have not earned any tokens yet. Try", "some of the activities around the guild to earn some.");
            setPhase(14);
        } else if(isPhase(14)) {
            //it.chatPlayer("Thanks!", 567)
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Ok, I'll go see what I can find.");
            setPhase(4);
        } else if(isPhase(13)) {
            send(DialogueType.NPC_STATEMENT, AJJAT, Expression.HAPPY, "Farewell warrior. Stay away from the dark side.");
            setPhase(4);
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Can you tell me about skillcapes, please?");
                setPhase(2);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Black Knight? Why are you here?");
                setPhase(5);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "What's the Dummy Room all about?");
                setPhase(7);
            } else if(option == 4) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "May I claim my tokens please?");
                setPhase(13);
            } else if(option == 5) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_THREE, "Bye!");
                setPhase(15);
            }
        }
    }
}
