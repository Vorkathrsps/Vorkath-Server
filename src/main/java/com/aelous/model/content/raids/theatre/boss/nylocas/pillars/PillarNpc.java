package com.aelous.model.content.raids.theatre.boss.nylocas.pillars;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.nylocas.Vasilias;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

public class PillarNpc extends NPC {
    PillarObject pillarObject;
    Vasilias vasilias;

    public PillarNpc(int id, Tile tile, PillarObject pillarObject, Vasilias vasilias) {
        super(id, tile);
        this.pillarObject = pillarObject;
        this.vasilias = vasilias;
        this.setSize(4);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        vasilias.pillarNpc.add(this);
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
                vasilias.pillarNpc.remove(this);
                o.setId(32864);
                vasilias.pillarObject.remove(o);
            });
        });
    }

}
