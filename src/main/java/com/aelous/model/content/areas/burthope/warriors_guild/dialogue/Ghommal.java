package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Skills;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.GHOMMAL;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Ghommal extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        int attack_lvl = player.getSkills().level(Skills.ATTACK);
        int strength_lvl = player.getSkills().level(Skills.STRENGTH);
        //Does our player have the requirements to enter the guild?
        if (attack_lvl + strength_lvl < 130) {
            send(DialogueType.NPC_STATEMENT, GHOMMAL, Expression.HAPPY, "You not pass. You too weedy.");
            setPhase(0);
        } else {
            send(DialogueType.NPC_STATEMENT, GHOMMAL, Expression.HAPPY, "Welcome you to Warrior Guild!");
            setPhase(8);
        }
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_FOUR, "What? But I'm a warrior!");
            setPhase(1);
        } else if (isPhase(1)) {
            send(DialogueType.NPC_STATEMENT, GHOMMAL, Expression.NODDING_FIVE, "Heehee... he say he warrior... I not heard that one", "for... at leas' 5 minutes!");
            setPhase(2);
        } else if (isPhase(2)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.BAD, "Go on, let me in, you know you want to. I could...", "make it worth your while...");
            setPhase(3);
        } else if (isPhase(4)) {
            send(DialogueType.NPC_STATEMENT, GHOMMAL, Expression.FURIOUS, "No! You is not a strong warrior, you not enter till you", "bigger. Ghommal does not take bribes.");
            setPhase(5);
        } else if (isPhase(5)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Why not?");
            setPhase(6);
        } else if (isPhase(6)) {
            send(DialogueType.NPC_STATEMENT,GHOMMAL, Expression.ANXIOUS, "Ghommal stick to Warrior's Code of Honour. When", "you a bigger, stronger warrior, you come back.");
            setPhase(7);
        } else if (isPhase(7)) {
            stop();
        } else if (isPhase(8)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Umm.. thank you, I think.");
            setPhase(7);
        }
    }
}
