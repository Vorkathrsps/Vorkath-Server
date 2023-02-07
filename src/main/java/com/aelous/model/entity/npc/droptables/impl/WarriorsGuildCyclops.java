package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen | March, 26, 2021, 18:59
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class WarriorsGuildCyclops implements Droptable {

    @Override
    public void reward(NPC killed, Player killer) {

        int defender = killer.getAttribOr(AttributeKey.WARRIORS_GUILD_CYCLOPS_ROOM_DEFENDER, 0);
        int chance = killer.getPlayerRights().isDeveloper(killer) ? 2 : 30;

        //All the way up to rune
        if(killed.id() == NpcIdentifiers.CYCLOPS_2464 || killed.id() == NpcIdentifiers.CYCLOPS_2465) {
            if (Utils.rollDie(chance, 1)) {
                var def = World.getWorld().definitions().get(ItemDefinition.class, defender);
                String aOrAn;
                if (defender == 8845 || defender == 8849)
                    aOrAn = "an";
                else
                    aOrAn = "a";

                //Defender drop
                drop(killed, killer, new Item(defender));
                killer.message("<col=804080>The Cyclops drops " + aOrAn + " " + def.name + ". Be sure to show this to Kamfreena to unlock");
                killer.message("<col=804080>the next tier Defender!");
            }

            //Normal drop
            var table = ScalarLootTable.forNPC(NpcIdentifiers.CYCLOPS_2464);
            if (table != null) {
                var reward = table.randomItem(World.getWorld().random());
                drop(killed, killer.tile(), killer, reward);
            }
        } else if (killed.id() == NpcIdentifiers.CYCLOPS_2137) {//Dragon defender
            chance = killer.getPlayerRights().isDeveloper(killer) ? 2 : 70;
            if (Utils.rollDie(chance, 1)) {
                //Defender drop
                drop(killed, killer, new Item(defender));
                var count = killer.<Integer>getAttribOr(AttributeKey.DRAGON_DEFENDER_DROPS, 0) + 1;
                killer.putAttrib(AttributeKey.DRAGON_DEFENDER_DROPS, count);
            }

            //Normal drop
            var table = ScalarLootTable.forNPC(NpcIdentifiers.CYCLOPS_2137);
            if (table != null) {
                var reward = table.randomItem(World.getWorld().random());
                drop(killed, killer.tile(), killer, reward);
            }
        }
    }
}
