package com.cryptic.model.content.raids.theatre.boss.verzik.nylocas;

import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

public class PurpleNylocas extends NPC {
    TheatreInstance theatreInstance;
    public PurpleNylocas(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
    }

    @Override
    public void postSequence() {

    }

    @Override
    public void die() {
        this.animate(8078);
        if (theatreInstance.getVerzikNylocasList() != null) {
            theatreInstance.getVerzikNylocasList().remove(this);
        }
        Chain.noCtx().runFn(2, this::remove);
    }
}
