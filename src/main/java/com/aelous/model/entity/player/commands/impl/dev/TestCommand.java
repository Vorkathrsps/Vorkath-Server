
package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class TestCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.startEvent(3, () -> {
            player.getPacketSender().sendMessage("hi... waited 3 ticks.. am i here?");
        }, 10, () -> {
            player.getPacketSender().sendMessage("hi.. waited 10 ticks..");
        });
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
