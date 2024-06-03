package com.cryptic.model.content.areas.burthope.warriors_guild.dialogue;

import com.cryptic.model.World;
import com.cryptic.clientscripts.impl.dialogue.Dialogue;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.LIDIO;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Lidio extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(LIDIO, Expression.CALM_TALK, "Greetings warrior, how can I fill your stomach today?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            sendPlayerChat(Expression.CALM_TALK, "With food preferably.");
            setPhase(1);
        } else if(isPhase(1)) {
            stop();
            World.getWorld().shop(25).open(player);
        }
    }
}
