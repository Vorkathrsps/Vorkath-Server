package com.cryptic.model.content.raids.theatre.boss.nylocas.pillars;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

public class PillarNpc extends NPC {
    GameObject pillarObject;
    TheatreInstance theatreInstance;
    public PillarNpc(int id, Tile tile, GameObject pillarObject, TheatreInstance theatreInstance) {
        super(id, tile);
        this.pillarObject = pillarObject;
        this.theatreInstance = theatreInstance;
        this.setSize(4);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        theatreInstance.getPillarList().add(this);
    }

    public void spawnPillarObject() {
        this.pillarObject.spawn();
        this.theatreInstance.getPillarObject().add(this.pillarObject);
    }

    @Override
    public void postSequence() {
        super.postSequence();

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        if (healthAmount <= 0.5D && healthAmount > 0) {
            this.pillarObject.setId(32863);
        }
    }

    @Override
    public void die() {
        this.remove();
        this.pillarObject.animate(8074);
        Chain.noCtx().delay(4, () -> {
            this.theatreInstance.getPillarList().remove(this);
            this.pillarObject.setId(32864);
            this.theatreInstance.getPillarObject().remove(pillarObject);
        });
    }

}
