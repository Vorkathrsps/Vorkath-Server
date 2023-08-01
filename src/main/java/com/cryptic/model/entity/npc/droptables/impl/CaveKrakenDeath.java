package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

/**
 * @author Patrick van Elderen | January, 03, 2021, 14:50
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class CaveKrakenDeath implements Droptable {

    @Override
    public void reward(NPC killed, Player killer) {
        var table = ScalarLootTable.forNPC(493);
        if (table != null) {
            var reward = table.randomItem(World.getWorld().random());
            if (reward != null) {
                drop(killed, killer.tile(), killer, reward);
            }

            drop(killed, killer.tile(), killer, new Item(526));

            table.getGuaranteedDrops().forEach(tableItem -> {
                drop(killed, killer.tile(), killer, new Item(tableItem.id, Utils.random(tableItem.min, tableItem.max)));
            });
        }
        killed.die();
    }
}
