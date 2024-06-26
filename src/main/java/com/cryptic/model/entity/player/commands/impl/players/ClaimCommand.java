package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.services.database.NewStore;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | May, 29, 2021, 11:13
 * 
 */
public class ClaimCommand implements Command {

    private long lastCommandUsed;

    @Override
    public void execute(Player player, String command, String[] parts) {
        new Thread(new NewStore(player)).start();

    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
