package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

import java.util.HashMap;

/**
 * @author PVE
 * @Since augustus 19, 2020
 */
public class DebugNpcsCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        HashMap<String, Integer> counts = new HashMap<>();
        World.getWorld().getNpcs().forEach(npc -> {
            Integer count = counts.getOrDefault(npc.spawnStack, 0);
            counts.put(npc.spawnStack, (count + 1));
        });
        Utils.sortByValue(counts, true);

        System.out.println("NPC STACKS:");
        counts.forEach((stack, count) -> System.out.println("    " + stack + ": " + count));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
