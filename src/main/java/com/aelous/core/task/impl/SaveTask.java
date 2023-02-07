package com.aelous.core.task.impl;

import com.aelous.GameServer;
import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;

/**
 * @author Ynneh
 */
public class SaveTask extends Task {

    private final Player player;

    public SaveTask(Player player) {
        super("SavePlayerTask", GameServer.properties().autosaveMinutes * 100);
        this.player = player;
    }

    @Override
    protected void execute() {
        if (!player.isRegistered()) {
            stop();
            return;
        }
        World.getWorld().ls.savePlayerAsync(player);
    }
}
