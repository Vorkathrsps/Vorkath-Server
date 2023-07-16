package com.aelous.model.content.raids.theatre.nylocas;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

public class Vasilias extends NPC {
    public Vasilias(int id, Tile tile, Player player) {
        super(id, tile);
        this.hidden(true);
        this.spawnDirection(6);
        this.getCombat().setTarget(null);
        Chain.noCtx().delay(1, () -> {
            this.hidden(false);
            this.lockNoDamage();
            this.animate(9030);
        }).then(2, () -> {
            this.unlock();
            this.getCombat().setTarget(player);
        });
    }

}
