package com.aelous.model.content.areas.burthope.rogues_den.dialogue;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.BRIAN_ORICHARD;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:34
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class BrianORichard extends Dialogue {
    
    public static final int MYSTIC_JEWEL = 5561;

    @Override
    protected void start(Object... parameters) {
        int DIALOGUE_STARTED = player.getAttribOr(AttributeKey.BRIAN_O_RICHARD_DIALOGUE, 0);
        if (DIALOGUE_STARTED == 0) {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.NODDING_THREE, "Hi there, looking for a challenge are you?");
            setPhase(0);
        } else {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.NODDING_THREE, "Hi again, what can I do for you?");
            setPhase(12);
        }
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes actually, what've you got?", "What is this place?", "No thanks.");
            setPhase(1);
        } else if(isPhase(2)) {
            //Does our player have at least 50 agility?
            if (player.getSkills().level(Skills.AGILITY) < 50) {
                send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.ON_ONE_HAND, "Shame, I don't think I have anything for you. Train up", "your Agility skill to at least 50 and I might be able to", "help you out.");
                setPhase(3);
            } else if (player.getSkills().level(Skills.THIEVING) < 50) {
                send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.ON_ONE_HAND, "Shame, I don't think I have anything for you. Train up", "your Thieving skill to at least 50 and I might be able to", "help you out.");
                setPhase(3);
            } else {
                player.putAttrib(AttributeKey.BRIAN_O_RICHARD_DIALOGUE, 1);
                send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.SHAKING_HEAD_THREE, "Aha, I have the perfect thing for you! See if you can", "get to the centre of my maze, the further you get the", "greater the rewards. There's even some special prizes if", "you make it right to the end.");
                setPhase(4);
            }
        } else if(isPhase(3)) {
            stop();
        } else if(isPhase(4)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Ok that sounds good!", "What is this place?", "Actually I think I'll pass thanks.");
            setPhase(5);
        } else if(isPhase(6)) {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.SHAKING_HEAD_ONE, "Great! You should take this jewel with you, it'll allow", "you to get out of the maze at any time. However that's", "all you're allowed to take in with you, no cheating!");
            setPhase(7);
        } else if(isPhase(7)) {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.BAD, "Oh one last thing, if you happen to see my harmonica", "I'd really like to have it back.");
            setPhase(3);
        } else if(isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.SHAKING_HEAD_ONE, "Ah welcome to my humble home, well actually it belongs", "to mummsie but she's getting on a bit so I look after", "the place for her.");
            setPhase(9);
        } else if(isPhase(9)) {
            send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.NODDING_THREE, "So are you interested in a challenge?");
            setPhase(10);
        } else if(isPhase(10)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes actually, what've you got?", "No thanks.");
            setPhase(11);
        } else if(isPhase(12)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I want to try the maze again!", "What is this place?", "Nothing thanks");
            setPhase(13);
        } else if(isPhase(14)) {
            if (player.inventory().contains(new Item(MYSTIC_JEWEL))) {
                send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.NODDING_THREE, "Great, well you've already got a jewel so away you go!");
                setPhase(3);
            } else {
                if (player.inventory().add(new Item(MYSTIC_JEWEL, 1), true)) {
                    send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.NODDING_THREE, "Certainly, here's another jewel, good luck!");
                } else {
                    send(DialogueType.NPC_STATEMENT, BRIAN_ORICHARD, Expression.ON_ONE_HAND, "You need more inventory space before I can give you a jewel!");
                }
                setPhase(3);
            }
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Yes actually, what've you got?");
                setPhase(2);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "What is this place?");
                setPhase(8);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "No thanks.");
                setPhase(3);
            }
        } else if(isPhase(5)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_THREE, "Ok that sounds good!");
                setPhase(6);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "What is this place?");
                setPhase(8);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "Actually I think I'll pass thanks.");
                setPhase(3);
            }
        } else if(isPhase(11)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Yes actually, what've you got?");
                setPhase(2);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "No thanks.");
                setPhase(3);
            }
        } else if(isPhase(13)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_THREE, "I want to try the maze again!");
                setPhase(14);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "What is this place?");
                setPhase(8);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.HAPPY, "No thanks.");
                setPhase(3);
            }
        }
    }
}
