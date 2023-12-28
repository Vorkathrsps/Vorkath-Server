package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;

import static com.cryptic.model.content.treasure.TreasureRewardCaskets.MASTER_CASKET;
import static com.cryptic.model.entity.attributes.AttributeKey.DOUBLE_DROP_LAMP_TICKS;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | January, 03, 2021, 14:37
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ZulrahDeath implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var table = ScalarLootTable.forNPC(2042);
        if (table != null) {
            drop(npc, new Tile(2262, 3072, killer.tile().level), killer, new Item(ZULRAHS_SCALES, World.getWorld().random(100, 300)));

            if (World.getWorld().rollDie(50, 1)) {
                drop(npc, new Tile(2262, 3072, killer.tile().level), killer, new Item(MASTER_CASKET,1));
                killer.message("<col=0B610B>You have received a treasure casket drop!");
            }

            var rolls = 2;
            var reward = table.rollItem();
            for (int i = 0; i < rolls; i++) {
                if (reward != null) {
                    boolean doubleDropsLampActive = (Integer) killer.getAttribOr(DOUBLE_DROP_LAMP_TICKS, 0) > 0;
                    if (doubleDropsLampActive) {
                        if(World.getWorld().rollDie(10, 1)) {
                            reward.setAmount(reward.getAmount() * 2);
                            killer.message("The double drop lamp effect doubled your drop.");
                        }
                    }
                    drop(npc, new Tile(2262, 3072, killer.tile().level), killer, reward);
                }
            }
        }
    }
}
