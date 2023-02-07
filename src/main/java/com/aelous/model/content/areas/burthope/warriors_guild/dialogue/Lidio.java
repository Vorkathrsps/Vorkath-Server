package com.aelous.model.content.areas.burthope.warriors_guild.dialogue;

import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.LIDIO;

/**
 * @author PVE
 * @Since juli 10, 2020
 */
public class Lidio extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        send(DialogueType.NPC_STATEMENT, LIDIO, Expression.CALM_TALK, "Greetings warrior, how can I fill your stomach today?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            send(DialogueType.PLAYER_STATEMENT, Expression.CALM_TALK, "With food preferably.");
            setPhase(1);
        } else if(isPhase(1)) {
            stop();
            World.getWorld().shop(25).open(player);
        }
    }
}
