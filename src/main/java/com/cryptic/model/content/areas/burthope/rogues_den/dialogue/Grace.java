package com.cryptic.model.content.areas.burthope.rogues_den.dialogue;

import com.cryptic.model.World;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.cs2.impl.dialogue.util.Expression;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.GRACE;

/**
 * @author Origin | March, 26, 2021, 09:32
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Grace extends Dialogue {

    public static boolean onNpcOption2(Player player, NPC npc) {
        if (npc.id() == GRACE) {
            World.getWorld().shop(21).open(player);
            return true;
        }
        return false;
    }

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(GRACE, Expression.NODDING_ONE, "What can I do for you?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if(isPhase(0)) {
            sendOption("What would you like to say?", "I don't know. What can you do for me?", "Can I see what you're selling?", "I'm alright, thanks.");
            setPhase(1);
        } else if(isPhase(2)) {
            sendNpcChat(GRACE, Expression.SNICKER, "A good question indeed! I'm selling special clothing for", "Agility enthusiasts.");
            setPhase(3);
        } else if(isPhase(3)) {
            sendNpcChat(GRACE, Expression.ANXIOUS, "Sometimes, when you're exploring the hidden rooftops of", "our cities, you'll find one of my Marks.");
            setPhase(4);
        } else if(isPhase(4)) {
            sendNpcChat(GRACE, Expression.NODDING_FIVE, "Once you've got enough Marks, I'll exchange them for", "my Graceful clothing. So, does that interest you?");
            setPhase(5);
        } else if(isPhase(5)) {
            sendOption("What would you like to say?", "Can I see what you're selling?", "I'm alright, thanks.");
            setPhase(6);
        } else if(isPhase(7)) {
            stop();
            World.getWorld().shop(21).open(player);
        } else if(isPhase(8)) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if(isPhase(1)) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "I don't know. What can you do for me?");
                setPhase(2);
            } else if(option == 2) {
                sendPlayerChat(Expression.NODDING_ONE, "Can I see what you're selling?");
                setPhase(7);
            } else if(option == 3) {
                sendPlayerChat(Expression.DULL_TWO, "I'm alright, thanks.");
                setPhase(8);
            }
        } else if(isPhase(6)) {
            if(option == 1) {
                sendPlayerChat(Expression.NODDING_ONE, "Can I see what you're selling?");
                setPhase(7);
            } else if(option == 2) {
                sendPlayerChat(Expression.DULL_TWO, "I'm alright, thanks.");
                setPhase(8);
            }
        }
    }
}
