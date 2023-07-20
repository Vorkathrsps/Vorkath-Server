package com.aelous.model.content.minigames.impl.tempoross.skilling;

import com.aelous.model.content.minigames.impl.tempoross.process.Tempoross;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

public class SpiritPool extends NPC {
    Player player;
    public SpiritPool(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }
    @Override
    public void postSequence() {
        if (!Tempoross.isActivatePools()) {
            this.die();
        }
    }
    @Override
    public void die() {
        this.transmog(10570);
    }

}
