package com.cryptic.model.content.areas.burthope.warriors_guild.dialogue;

import com.cryptic.model.World;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.LILLY;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Lilly extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(LILLY, Expression.CALM_TALK, "Uh..... Hi... didn't see you there. Can... I help?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            sendPlayerChat(Expression.CALM_TALK, "Umm... do you sell potions?");
            setPhase(1);
        } else if(isPhase(1)) {
            sendNpcChat(LILLY, Expression.CALM_TALK, "Erm... yes. When I'm not drinking them.");
            setPhase(2);
        } else if(isPhase(2)) {
            sendOption(DEFAULT_OPTION_TITLE, "I'd like to see what you have for sale.", "That's a pretty wall hanging.", "Bye!");
            setPhase(3);
        } else if(isPhase(4)) {
            sendNpcChat(LILLY, Expression.CALM_TALK, "Of course...");
            setPhase(5);
        } else if(isPhase(5)) {
            stop();
            World.getWorld().shop(26).open(player);
        } else if(isPhase(6)) {
            sendNpcChat(LILLY, Expression.CALM_TALK, "Do you think so? I made it myself.");
            setPhase(7);
        } else if(isPhase(7)) {
            sendPlayerChat(Expression.NODDING_ONE, "Really? Is that why there's all this cloth and dye around?");
            setPhase(8);
        } else if(isPhase(8)) {
            sendNpcChat(LILLY, Expression.CALM_TALK, "Yes, it's a hobby of mine when I'm... relaxing.");
            setPhase(9);
        } else if(isPhase(9)) {
            stop();
        } else if(isPhase(10)) {
            sendNpcChat(LILLY, Expression.CALM_TALK, "Have fun and come back soon!");
            setPhase(9);
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(3)) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "I'd like to see what you have for sale.");
                setPhase(4);
            } else if(option == 2) {
                sendPlayerChat(Expression.NODDING_ONE, "That's a pretty wall hanging.");
                setPhase(6);
            } else if(option == 3) {
                sendPlayerChat(Expression.NODDING_ONE, "Bye!");
                setPhase(10);
            }
        }
    }
}
