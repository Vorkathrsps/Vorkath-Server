package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

import static com.cryptic.model.entity.attributes.AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE;

public class SpawnNPCCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) { // ID HP AMOUNT RESPAWN=1
        int amt = parts.length > 3 ? Integer.parseInt(parts[3]) : 1;
        boolean respawn = (parts.length > 4 ? Integer.parseInt(parts[4]) : 0) == 1;
        for (int i = 0; i < amt; i++) {
            NPC npc = NPC.of(Integer.parseInt(parts[1]), player.tile()).respawns(respawn);
            World.getWorld().registerNpc(npc);
            if (parts.length > 2) {
                npc.setHitpoints(Integer.parseInt(parts[2]));
                npc.getCombatInfo().stats.hitpoints = npc.hp();
            }
            npc.putAttrib(ATTACKING_ZONE_RADIUS_OVERRIDE, 25); // follow for a long time
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isOwner(player));
    }

}
