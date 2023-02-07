package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;

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
        killed.transmog(-1);
    }
}
