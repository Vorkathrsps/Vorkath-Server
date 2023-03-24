package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick van Elderen | November, 25, 2020, 18:56
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WildernessKeyCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(WildernessKeyCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
