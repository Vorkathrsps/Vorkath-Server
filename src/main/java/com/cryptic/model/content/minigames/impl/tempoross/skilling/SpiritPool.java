package com.cryptic.model.content.minigames.impl.tempoross.skilling;

import com.cryptic.model.content.minigames.impl.tempoross.process.Tempoross;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;

public class SpiritPool extends NPC {
    Player player;
    public SpiritPool(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }
    @Override
    public void combatSequence() {
        if (!Tempoross.isActivatePools()) {
            this.die();
        }
    }
    @Override
    public void die() {
        this.transmog(10570, false);
    }

}
