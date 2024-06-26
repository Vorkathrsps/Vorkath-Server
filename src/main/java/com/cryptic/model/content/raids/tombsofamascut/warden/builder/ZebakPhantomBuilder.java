package com.cryptic.model.content.raids.tombsofamascut.warden.builder;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatreofblood.controller.RaidBuilder;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.content.raids.tombsofamascut.warden.npcs.ZebakNPC;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class ZebakPhantomBuilder implements RaidBuilder {
    @Override
    public void build(Player player, InstancedArea instance) {
        if (instance instanceof TombsInstance tombsInstance) {
            ZebakNPC zebak = new ZebakNPC(11774, new Tile(3943, 5153, tombsInstance.getzLevel() + 1), Direction.NORTH_WEST.toInteger(), tombsInstance);
            zebak.setHitpoints(2500);
            zebak.setInstancedArea(tombsInstance);
            zebak.spawn(false);
        }
    }

    @Override
    public int scale(NPC npc, Player player, boolean hardMode) {
        return 0;
    }
}
