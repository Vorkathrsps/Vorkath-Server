package com.cryptic.model.content.raids.tombsofamascut.warden.builder;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.controller.RaidBuilder;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.content.raids.tombsofamascut.warden.npcs.KephriNPC;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class KephriPhantomBuilder implements RaidBuilder {
    @Override
    public void build(Player player, InstancedArea instance) {
        if (instance instanceof TombsInstance tombsInstance) {
            KephriNPC kephri = new KephriNPC(11776, new Tile(3926, 5153, tombsInstance.getzLevel() + 1), Direction.NORTH_EAST.toInteger(), tombsInstance);
            kephri.setHitpoints(2500);
            kephri.setInstancedArea(tombsInstance);
            kephri.spawn(false);
        }
    }

    @Override
    public int scale(NPC npc, Player player, boolean hardMode) {
        return 0;
    }
}
