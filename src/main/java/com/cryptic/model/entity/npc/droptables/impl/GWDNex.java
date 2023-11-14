package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.utility.Color;

import java.util.concurrent.TimeUnit;

import static com.cryptic.model.content.collection_logs.LogType.BOSSES;

/**
 * @Author Origin
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
                        Item reward = table.rollItem();
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
