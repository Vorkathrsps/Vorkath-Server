package com.cryptic.model.content.events;

import com.cryptic.model.entity.player.Player;

public class PlayerEvent {
    final Player player;
    final Events event;
    int time;

    public PlayerEvent(Player player, Events event, int time) {
        this.player = player;
        this.event = event;
        this.time = time;
    }

    public PlayerEvent start() {
        int currentTicks = getCurrentTicks();
        player.putAttrib(this.event.attribute, true);
        player.getTimers().extendOrRegister(this.event.key, getTicks(currentTicks));
        player.getPacketSender().sendEffectTimer(round(currentTicks), this.event.timer);
        return this;
    }

    int getCurrentTicks() {
        return player.getTimers().left(this.event.key);
    }

    int getTicks(int currentTicks) {
        return this.time + currentTicks;
    }

    int round(int left) {
        if (left > 0) this.time += left;
        return (int) Math.round(this.time * 0.6);
    }
}
