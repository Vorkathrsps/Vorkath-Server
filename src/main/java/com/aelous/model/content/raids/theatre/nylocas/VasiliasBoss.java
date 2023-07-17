package com.aelous.model.content.raids.theatre.nylocas;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.nylocas.state.VasiliasState;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
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
