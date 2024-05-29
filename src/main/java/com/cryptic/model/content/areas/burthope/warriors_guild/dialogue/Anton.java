package com.cryptic.model.content.areas.burthope.warriors_guild.dialogue;

import com.cryptic.model.World;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.ANTON;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Anton extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(ANTON, Expression.CALM_TALK, "Ahhh, hello there. How can I help?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            sendPlayerChat(Expression.CALM_TALK, "Appearance like you have a good selection", "of weapons around here...");
            setPhase(1);
        } else if(isPhase(1)) {
            sendNpcChat(ANTON, Expression.CALM_TALK, "Indeed so, specially imported from the finest smiths around", "the lands, take a look at my wares.");
            setPhase(2);
        } else if(isPhase(2)) {
            stop();
            World.getWorld().shop(24).open(player);
        }
    }
}
