package com.cryptic.model.content.areas.burthope.rogues_den.dialogue;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.inter.dialogue.Expression;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.EMERALD_BENEDICT;

/**
 * @author Origin | March, 26, 2021, 09:35
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class EmeraldBenedict extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendNpcChat(EMERALD_BENEDICT, Expression.EVIL, "Win any tournaments lately?");
        setPhase(0);
    }

    @Override
    protected void next() {
        if (isPhase(0)) {
            sendOption(DEFAULT_OPTION_TITLE, "Bank", "Extra pin settings", "Yes, but I'd like to collect items now.", "Nevermind.");
            setPhase(1);
        } else if (isPhase(2)) {
            stop();
        }
    }

    @Override
    protected void select(int option) {
        if (isPhase(1)) {
            if (option == 1) {
                stop();
                player.getBank().open();
            } else if (option == 2) {
                player.getBankPinSettings().open(EMERALD_BENEDICT);
            } else if (option == 3) {
                sendNpcChat(EMERALD_BENEDICT, Expression.ON_ONE_HAND, "Use the shop option.");
                setPhase(2);
            } else if (option == 4) {
                sendPlayerChat(Expression.NODDING_FOUR, "Yes, and I'll keep hold of my points.");
                setPhase(2);
            }
        }
    }
}
