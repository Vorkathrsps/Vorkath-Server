package com.cryptic.model.content.raids.theatre.boss.nylocas;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.boss.nylocas.state.VasiliasState;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

public class VasiliasBoss extends NPC { //TODO add death animation sequence
    @Getter @Setter public VasiliasState vasiliasState;

    public VasiliasBoss(int id, Tile tile, Player player, VasiliasState vasiliasState) {
        super(id, tile);
        this.hidden(true);
        this.spawnDirection(6);
        this.getCombat().setTarget(null);
        this.vasiliasState = vasiliasState;
        Chain.noCtx().delay(1, () -> {
            this.hidden(false);
            this.lockNoDamage();
            this.animate(9030);
        }).then(2, () -> {
            this.unlock();
            this.getCombat().setTarget(player);
        });
    }

    @Override
    public void die() {
        this.setVasiliasState(VasiliasState.DEAD);
        World.getWorld().unregisterNpc(this);
    }

}
