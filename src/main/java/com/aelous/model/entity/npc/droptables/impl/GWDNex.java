package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.utility.Color;

import java.util.concurrent.TimeUnit;

import static com.aelous.model.content.collection_logs.LogType.BOSSES;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 12, 2022
 */
public class GWDNex implements Droptable {//TODO redunant class remove this class

    private void drop(Entity entity) {
        entity.getCombat().getDamageMap().forEach((key, hits) -> {
            Player player = (Player) key;
            player.message(Color.RED.wrap("You've dealt " + hits.getDamage() + " damage to Nex!"));
            if (entity.tile().isWithinDistance(player.tile(),10) && hits.getDamage() >= 100) {
                if(entity instanceof NPC) {
                    player.message("You received a drop roll from the table for dealing at least 100 damage!");
                    NPC npc = entity.getAsNpc();

                    //Always log kill timers
                    player.getBossTimers().submit(npc.def().name, (int) player.getCombat().getFightTimer().elapsed(TimeUnit.SECONDS), player);

                    //Always increase kill counts
                    player.getBossKillLog().addKill(npc);

                    //Random drop from the table
                    ScalarLootTable table = ScalarLootTable.forNPC(npc.id());
                    if (table != null) {
                        Item reward = table.randomItem(World.getWorld().random());
                        if (reward != null) {

                            // bosses, find npc ID, find item ID
                            BOSSES.log(player, npc.id(), reward);

                            GroundItemHandler.createGroundItem(new GroundItem(reward, npc.tile(), player));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void reward(NPC npc, Player killer) {
        drop(npc);
    }
}
