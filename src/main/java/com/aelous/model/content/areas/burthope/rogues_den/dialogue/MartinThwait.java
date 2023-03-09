package com.aelous.model.content.areas.burthope.rogues_den.dialogue;

import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Skills;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.MARTIN_THWAIT;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:36
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MartinThwait extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.ANNOYED, "You know it's sometimes funny how things work out, I", "lose some gold but find an item, or I lose an item and", "find some gold... no-one ever knows what's gone where", "ya know.");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yeah I know what you mean, found anything recently?", "Can you tell me about your cape?", "Okay... I'll be going now.");
            setPhase(1);
        } else if(isPhase(2)) {
            stop();
        } else if(isPhase(3)) {
            send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.SHAKING_HEAD_THREE, "Certainly! Skillcapes are a symbol of achievement. Only", "people who have mastered a skill and reached level 99", "can get their hands on them and gain the benefits they", "carry.");
            setPhase(4);
        } else if(isPhase(4)) {
            send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.CALM_TALK, "The Cape of Thieving provides a nice boost to your", "chances of pickpocketing when worn. Is there anything", "else I can help you with?");
            setPhase(5);
        } else if(isPhase(5)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Have you found anything recently?", "No thank you.");
            setPhase(6);
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                //Does our player have at least 50 agility?
                if (player.getSkills().level(Skills.AGILITY) < 50) {
                    send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.BAD, "Sorry, mate. Train up your Agility skill to at least", "50 and I might be able to help you out.");
                    setPhase(2);
                } else if (player.getSkills().level(Skills.THIEVING) < 50) {
                    send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.BAD, "Sorry, mate. Train up your Thieving skill to at least", "50 and I might be able to help you out.");
                    setPhase(2);
                } else {
                    World.getWorld().shop(22).open(player);
                }
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Can you tell me about your cape?");
                setPhase(3);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_FOUR, "Okay... I'll be going now.");
                setPhase(2);
            }
        } else if(isPhase(6)) {
            if(option == 1) {
                //Does our player have at least 50 agility?
                if (player.getSkills().level(Skills.AGILITY) < 50) {
                    send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.BAD, "Sorry, mate. Train up your Agility skill to at least", "50 and I might be able to help you out.");
                    setPhase(2);
                } else if (player.getSkills().level(Skills.THIEVING) < 50) {
                    send(DialogueType.NPC_STATEMENT,MARTIN_THWAIT, Expression.BAD, "Sorry, mate. Train up your Thieving skill to at least", "50 and I might be able to help you out.");
                    setPhase(2);
                } else {
                    World.getWorld().shop(22).open(player);
                }
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "No thank you.");
                setPhase(2);
            }
        }
    }
}
