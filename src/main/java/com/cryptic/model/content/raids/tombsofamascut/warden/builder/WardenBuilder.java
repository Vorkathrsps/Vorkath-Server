package com.cryptic.model.content.raids.tombsofamascut.warden.builder;

import com.cryptic.model.content.instance.InstancedArea;
import com.cryptic.model.content.raids.theatre.controller.RaidBuilder;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.content.raids.tombsofamascut.warden.npcs.WardenNPC;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class WardenBuilder implements RaidBuilder {
    @Override
    public void build(Player player, InstancedArea instance) {
        if (instance instanceof TombsInstance tombsInstance) {
            WardenNPC warden = new WardenNPC(11762, new Tile(3934, 5152, tombsInstance.getzLevel() + 1), Direction.NORTH.toInteger(), tombsInstance);
            warden.setHitpoints(2500);
            warden.setInstancedArea(tombsInstance);
            warden.spawn(false);
        }
    }

    @Override
    public int scale(NPC npc, Player player, boolean hardMode) {
        return 0;
    }
}
