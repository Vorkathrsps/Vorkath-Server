package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.services.database.NewStore;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen | May, 29, 2021, 11:13
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
