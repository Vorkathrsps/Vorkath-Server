package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.commands.impl.staff.admin.KillCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetHitPointsCommand implements Command {

    private static final Logger logger = LogManager.getLogger(KillCommand.class);
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setHitpoints(1);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isOwner(player));
    }

}
