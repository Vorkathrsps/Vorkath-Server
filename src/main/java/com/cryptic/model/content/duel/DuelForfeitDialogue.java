package com.cryptic.model.content.duel;

import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.utility.timers.TimerKey;

public class DuelForfeitDialogue extends Dialogue {

    @Override
    protected void start(Object... parameters) {
        sendStatement("Are you sure you wish to forfeit?");
        setPhase(0);
    }

    @Override
    public void next() {
        if (isPhase(0)) {
            sendOption(DEFAULT_OPTION_TITLE, "Yes", "No");
            setPhase(1);
        }
    }

    @Override
    public void select(int option) {
        if (isPhase(1)) {
            switch (option) {
                case 1 -> {
                    // We could be in the middle of counting down. Better stop.
                    player.getTimers().cancel(TimerKey.STAKE_COUNTDOWN);
                    player.getDueling().getOpponent().getTimers().cancel(TimerKey.STAKE_COUNTDOWN);
                    player.getDueling().onDeath();
                    stop();
                }
                case 2 -> stop();
            }
        }
    }
}


