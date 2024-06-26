package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SCORPIA;

/**
 * @Author Origin
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
