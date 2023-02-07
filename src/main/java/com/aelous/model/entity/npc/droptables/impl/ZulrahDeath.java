package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;

import static com.aelous.model.content.treasure.TreasureRewardCaskets.MASTER_CASKET;
import static com.aelous.model.entity.attributes.AttributeKey.DOUBLE_DROP_LAMP_TICKS;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | January, 03, 2021, 14:37
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
            var reward = table.randomItem(World.getWorld().random());
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

            // Slayer unlock
            if (killer.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.DOUBLE_DROP_CHANCE) && World.getWorld().rollDie(100, 1)) {
                killer.message("The Double drops perk grants you a second drop!");
                if (reward != null) {
                    drop(npc, new Tile(2262, 3072, killer.tile().level), killer, reward);
                }
            }
        }
    }
}
