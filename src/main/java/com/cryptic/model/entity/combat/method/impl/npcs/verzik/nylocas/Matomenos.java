package com.cryptic.model.entity.combat.method.impl.npcs.verzik.nylocas;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

public class Matomenos extends NPC {
    public Matomenos(int id, Tile tile, boolean spawn) {
        super(id, tile, spawn);
        this.animate(8079);
        Chain.noCtx().runFn(15, this::die);
    }

    @Override
    public void die() {
        Chain.bound(this).runFn(1, () -> npc().animate(8078)).then(2, () -> npc().remove());
    }
}
