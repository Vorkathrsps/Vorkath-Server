package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.utility.Tuple;

/**
 * @author Origin | January, 29, 2021, 09:54
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RuniteGolem implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var trunk = new NPC(NpcIdentifiers.ROCKS_6601, npc.tile());
        World.getWorld().registerNpc(trunk);
        trunk.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(killer.getIndex(), killer));

        trunk.runUninterruptable(100, () -> {
            if (!trunk.dead()) { // Lives for exactly a minute
                World.getWorld().unregisterNpc(trunk);
            }
        });
    }
}
