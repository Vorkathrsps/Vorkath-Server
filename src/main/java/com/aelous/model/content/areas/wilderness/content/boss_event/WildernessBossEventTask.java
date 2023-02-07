package com.aelous.model.content.areas.wilderness.content.boss_event;

import com.aelous.core.task.Task;

import static com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent.BOSS_EVENT_INTERVAL;

/**
 * @author Patrick van Elderen | February, 13, 2021, 09:11
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WildernessBossEventTask extends Task {

    public WildernessBossEventTask() {
        super("WildernessBossEventTask", BOSS_EVENT_INTERVAL,true);
    }

    @Override
    protected void execute() {
        WildernessBossEvent wildernessBossEvent = WildernessBossEvent.getINSTANCE();
        wildernessBossEvent.startBossEvent();
    }
}
