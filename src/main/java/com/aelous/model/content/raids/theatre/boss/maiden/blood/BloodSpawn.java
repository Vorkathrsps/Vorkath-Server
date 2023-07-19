package com.aelous.model.content.raids.theatre.boss.maiden.blood;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.maiden.objects.BloodSplat;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import static com.aelous.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

public class BloodSpawn extends NPC {
    Player player;

    public BloodSpawn(int id, Tile tile, Player player) {
        super(id, tile);
        orbSpawns.add(this);
        this.player = player;
        this.walkRadius(4);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    public void clear() {
        die();
    }

    @Override
    public void die() {
        orbSpawns.clear();
        World.getWorld().unregisterNpc(this);
    }
}
