package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.LILLY;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Lilly extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Uh..... Hi... didn't see you there. Can... I help?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.CALM_TALK, "Umm... do you sell potions?");
            setPhase(1);
        } else if(isPhase(1)) {
            send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Erm... yes. When I'm not drinking them.");
            setPhase(2);
        } else if(isPhase(2)) {
            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "I'd like to see what you have for sale.", "That's a pretty wall hanging.", "Bye!");
            setPhase(3);
        } else if(isPhase(4)) {
            send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Of course...");
            setPhase(5);
        } else if(isPhase(5)) {
            stop();
            World.getWorld().shop(26).open(player);
        } else if(isPhase(6)) {
            send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Do you think so? I made it myself.");
            setPhase(7);
        } else if(isPhase(7)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Really? Is that why there's all this cloth and dye around?");
            setPhase(8);
        } else if(isPhase(8)) {
            send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Yes, it's a hobby of mine when I'm... relaxing.");
            setPhase(9);
        } else if(isPhase(9)) {
            stop();
        } else if(isPhase(10)) {
            send(DialogueType.NPC_STATEMENT, LILLY, Expression.CALM_TALK, "Have fun and come back soon!");
            setPhase(9);
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(3)) {
            if(option == 1) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "I'd like to see what you have for sale.");
                setPhase(4);
            } else if(option == 2) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "That's a pretty wall hanging.");
                setPhase(6);
            } else if(option == 3) {
                send(DialogueType.PLAYER_STATEMENT, Expression.NODDING_ONE, "Bye!");
                setPhase(10);
            }
        }
    }
}
