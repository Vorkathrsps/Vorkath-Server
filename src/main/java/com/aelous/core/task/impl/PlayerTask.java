package com.aelous.core.task.impl;

import com.aelous.core.task.Task;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.PacketListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kaleem on 19/07/2017.
 */
public class PlayerTask extends Task {

    //TODO: Various triggers

    protected final Player player;

    protected final Runnable action;

    protected final Runnable stopAction;

    private final Set<Class<? extends PacketListener>> stopTriggers = new HashSet<>();

    private final Set<Runnable> onEnd = new HashSet<>();

    public PlayerTask(int delay, Player player, Runnable action) {
        super("PlayerTask", delay, player, false);
        this.player = player;
        this.action = action;
        this.stopAction = this::stop;
    }

    public PlayerTask(int delay, Player player, Runnable action, boolean immediate) {
        super("PlayerTask", delay, player, immediate);
        this.player = player;
        this.action = action;
        this.stopAction = this::stop;
    }

    public final void stop() {
        setEventRunning(false);
        stopTriggers.forEach(clazz -> player.getTriggers().remove(clazz, stopAction));
        onEnd.forEach(Runnable::run);
        player.setCurrentTask(null);
    }

    public final void onEnd(Runnable action) {
        onEnd.add(action);
    }

    public void stopUpon(Class<? extends PacketListener> clazz) {
        stopTriggers.add(clazz);
        player.getTriggers().add(clazz, stopAction);
    }

    public boolean stops(Class<? extends PacketListener> clazz) {
        return stopTriggers.contains(clazz);
    }

    public void execute() {
        action.run();
    }

    public Player getPlayer() {
        return player;
    }

}
