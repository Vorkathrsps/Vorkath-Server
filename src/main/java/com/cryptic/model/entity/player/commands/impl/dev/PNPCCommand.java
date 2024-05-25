package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class PNPCCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int id = Integer.parseInt(parts[1]);
        if(id == -1) {
            player.looks().transmog(-1);
            player.looks().resetRender();
            player.message("You return to your human-like state.");
            return;
        }
        player.looks().transmog(id);
        player.message("You transmog into the "+World.getWorld().definitions().get(NpcDefinition.class, id).name+ ".");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
