package com.cryptic.model.content.raids.theatre.boss.nylocas.pillars;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

public class PillarNpc extends NPC {
    PillarObject pillarObject;
    TheatreInstance theatreInstance;

    public PillarNpc(int id, Tile tile, PillarObject pillarObject, TheatreInstance theatreInstance) {
        super(id, tile);
        this.pillarObject = pillarObject;
        this.theatreInstance = theatreInstance;
        this.setSize(4);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        theatreInstance.getPillarList().add(this);
    }

    public GameObject spawnPillarObject() {
        return pillarObject.spawn();
    }

    @Override
    public void postSequence() {
        super.postSequence();

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        if (healthAmount <= 0.5D && healthAmount > 0) {
            pillarObject.setId(32863);
        }
    }

    @Override
    public void die() {
        MapObjects.get(pillarObject.getId(), pillarObject.tile()).ifPresent(o -> {
            o.animate(8074);
            Chain.noCtx().delay(4, () -> {
                this.die();
                World.getWorld().unregisterNpc(this);
                theatreInstance.pillarList.remove(this);
                o.setId(32864);
                theatreInstance.getPillarObject().remove(o);
            });
        });
    }

}
