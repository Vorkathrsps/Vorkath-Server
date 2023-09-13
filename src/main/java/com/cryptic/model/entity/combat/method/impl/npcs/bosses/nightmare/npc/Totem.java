package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.npc;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

public class Totem extends NPC {
    public Totem(int id, Tile tile) {
        super(id, tile);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    @Override
    public void postSequence() {
        player().getPacketSender().sendProgressBar(75004, this.hp());
        player().getPacketSender().sendProgressBar(75006, this.hp());
        player().getPacketSender().sendProgressBar(75008, this.hp());
        player().getPacketSender().sendProgressBar(75010, this.hp());
    }
}
