package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.ZarosGodwars;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;


public class NexCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.teleport(new Tile(2905, 5203, 0));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
