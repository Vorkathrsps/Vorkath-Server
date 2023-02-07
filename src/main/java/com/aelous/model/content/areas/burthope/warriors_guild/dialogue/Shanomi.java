package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SHANOMI;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Shanomi extends Dialogue {

    private static final List<String> message = Arrays.asList("Those things which cannot be seen, perceive them.",
        "Do nothing which is of no use.",
        "Think not dishonestly.",
        "The Way in training is.",
        "Gain and loss between you must distinguish.",
        "Trifles pay attention even to.",
        "Way of the warrior this is.",
        "Acquainted with every art become.",
        "Ways of all professions know you.");

    public static void shoutMessage(NPC npc) {
        TaskManager.submit(new Task("shanomi_shout_task", 5) {
            @Override
            protected void execute() {
                npc.forceChat(message.get(Utils.random(message.size() - 1)));
            }
        });
    }

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "Greetings, " + player.getUsername() + ". Welcome you are in the test", "of combat.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "What do I do here?", "Where do the machines come from?", "What items can I get from here?", "Bye!");
            setPhase(1);
        } else if (isPhase(2)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "A spare suit of plate armour need you will. Full helm,", "plate leggings and platebody, yes? Placing it in the", "centre of the magical machines you will be doing.", "KA-POOF! The armour, it attack most furiously as if");
            setPhase(3);
        } else if (isPhase(3)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "alive! Kill it you must, yes.");
            setPhase(4);
        } else if (isPhase(4)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "So I use a full set of armour on the centre plate of", "the machines and it will animate it? Then I have to", "kill my our armour... how bizarre!");
            setPhase(5);
        } else if (isPhase(5)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "Yes. It is as you are saying. For this earn tokens", "you will. Also gain experience in combat you will.", "Trained long and here here have I.");
            setPhase(6);
        } else if (isPhase(6)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "You're not from around here are you...?");
            setPhase(7);
        } else if (isPhase(7)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "It is as you say.");
            setPhase(8);
        } else if (isPhase(8)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "So will I lose my armour?");
            setPhase(9);
        } else if (isPhase(9)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "Lose armour you will if damaged too much it becomes.", "Rare this is, but still possible. If kill you the armour", "does, also lose armour you will.");
            setPhase(10);
        } else if (isPhase(10)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "So, occasionally I might lose a bit because it's being", "based about and I'll obviously lose it if I die.. that it?");
            setPhase(11);
        } else if (isPhase(11)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "It is as you say.");
            setPhase(12);
        } else if (isPhase(12)) {
            stop();
        } else if (isPhase(13)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "Make them I did, with magics.");
            setPhase(14);
        } else if (isPhase(14)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Magic, in the Warrior's Guild?");
            setPhase(15);
        } else if (isPhase(15)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "A skilled warrior also am I. Harrallak mistakes does", "not make. Potential in my invention he sees and", "opportunity grasps.");
            setPhase(16);
        } else if (isPhase(16)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "I see, so you made the magical machines", "and Harrallak saw how they could be used in the guild", "train warrior's combat... interesting.", "Harrallak certainly is an intelligent guy.");
            setPhase(17);
        } else if (isPhase(17)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "It is as you say.");
            setPhase(12);
        } else if (isPhase(18)) {
            send(DialogueType.NPC_STATEMENT, SHANOMI, Expression.CALM_TALK, "Bye!");
            setPhase(12);
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(1)) {
            if (option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "What do I do here?");
                setPhase(2);
            } else if (option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Where do the machines come from?");
                setPhase(13);
            } else if (option == 3) {
                //TODO
            } else if (option == 4) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Bye!");
                setPhase(18);
            }
        }
    }
}
