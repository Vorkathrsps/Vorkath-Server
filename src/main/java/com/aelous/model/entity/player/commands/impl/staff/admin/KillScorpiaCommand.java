package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SCORPIA;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 19, 2021
 */
public class KillScorpiaCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        for(NPC npc : World.getWorld().getNpcs()) {
            if(npc == null) continue;

            if(npc.id() == SCORPIA) {
                npc.hit(player, npc.maxHp());
                player.message("Scorpia found and killed.");
                break;//Found scorpia break the loop
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
