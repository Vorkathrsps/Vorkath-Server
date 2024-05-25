package com.cryptic.core.task.impl;

import com.cryptic.GameServer;
import com.cryptic.core.task.Task;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.save.PlayerSaves;

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
        PlayerSaves.requestSave(player);
    }
}
