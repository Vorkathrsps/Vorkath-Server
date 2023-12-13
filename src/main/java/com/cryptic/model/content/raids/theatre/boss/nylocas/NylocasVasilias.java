package com.cryptic.model.content.raids.theatre.boss.nylocas;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.nylocas.state.VasiliasState;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.content.raids.theatre.stage.TheatreStage;
import com.cryptic.model.entity.combat.method.impl.npcs.vasilas.VasiliasCombat;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

public class NylocasVasilias extends NPC { //TODO add death animation sequence
    @Getter @Setter public VasiliasState vasiliasState;
    TheatreInstance theatreInstance;
    @Getter int finalInterpolatedTransmog;
    public NylocasVasilias(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.setCombatMethod(new VasiliasCombat());
        this.spawnDirection(6);
        this.getCombat().setTarget(null);
        this.theatreInstance = theatreInstance;
        Chain.noCtx().delay(1, () -> {
            this.hidden(false);
            this.lockNoDamage();
            this.animate(9030);
        }).then(2, () -> {
            this.unlock();
            this.getCombat().setTarget(theatreInstance.getOwner());
        });
    }

    @Override
    public void die() {
        theatreInstance.getTheatrePhase().setStage(TheatreStage.THREE);
        for (var p : theatreInstance.getPlayers()) {
            p.setRoomState(RoomState.COMPLETE);
        }
        World.getWorld().unregisterNpc(this);
    }

}
