package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.combat.skull.SkullType;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;

public class SkullCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(player.getSkullType() == SkullType.RED_SKULL) {
            return;
        }

        if (parts[0].startsWith("red")) {
            Skulling.assignSkullState(player, SkullType.RED_SKULL);
            player.message(Color.RED.tag()+"Be careful whilst being redskulled all items are lost on death, this includes pets!");
        } else {
            Skulling.assignSkullState(player, SkullType.WHITE_SKULL);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
