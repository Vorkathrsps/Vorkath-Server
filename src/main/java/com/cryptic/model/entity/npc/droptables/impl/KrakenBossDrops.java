package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.npc.droptables.ScalarLootTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Area;
import com.cryptic.utility.Utils;

import java.util.ArrayList;

/**
 * @author Origin | January, 03, 2021, 14:56
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class KrakenBossDrops implements Droptable {

    @Override
    public void reward(NPC killed, Player killer) {
        var table = ScalarLootTable.forNPC(494);
        if (table != null) {
            var loots = new ArrayList<String>();
            table.getGuaranteedDrops().forEach(tableItem -> {
                drop(killed, killer.tile(), killer, new Item(tableItem.id, Utils.random(tableItem.min, tableItem.max)));
                loots.add(lootname(tableItem.convert(), killer.getUsername()));
            });

            var rolls = 1;
            for (int i = 0; i < rolls; i++) {
                Item reward = table.randomItem(World.getWorld().random());
                if (reward != null) {
                    drop(killed, killer.tile(), killer, reward);
                    loots.add(lootname(reward, killer.getUsername()));
                }
            }

            var room = new Area(killer.tile().regionCorner(), killer.tile().regionCorner().transform(64, 64));
            // Broadcast to everyone in the normal kraken room.
            World.getWorld().getPlayers().forEachInArea(room, other -> {
                for (String lootmsg : loots) {
                    other.message(lootmsg);
                }
            });

        }
    }

    private String lootname(Item convert, String looterName) {
        return String.format("<col=0B610B>%s received a drop: %s.", looterName, convert.unnote().getAmount() == 1 ? convert.unnote().name() : "" + convert.getAmount() + " x " + convert.unnote().name());
    }
}
