package com.aelous.model.content.raids.theatre.nylocas.pillars;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PillarSpawn extends NPC {
    @Nonnull
    @Getter
    GameObject gameObject;
    @Getter
    public static List<NPC> npcs = new ArrayList<>();
    @Getter
    public static List<GameObject> pillars = new ArrayList<>();
    public PillarSpawn(int id, Tile tile, @NotNull GameObject gameObject) {
        super(id, tile);
        this.gameObject = gameObject;
    }
    public void spawnPillarNpc() {
        this.spawn(false);
        World.getWorld().registerNpc(this);
        getNpcs().add(this);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }
    public void spawnPillarObject() {
        getGameObject().spawn();
        getPillars().add(getGameObject());
    }
    public void updatePillarObject(int newID) {
        MapObjects.get(getGameObject().getId(), getGameObject().tile()).ifPresent(o -> {
            o.setId(newID);
            this.gameObject = o;
            getPillars().add(o);
        });
    }
    @Override
    public void postSequence() {
        super.postSequence();

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        if (healthAmount <= 0.5D && healthAmount > 0) {
            updatePillarObject(32863);
        }
    }
    @Override
    public void die() {
        Chain.bound(null).runFn(1, () -> {
            getGameObject().animate(8074);
        }).then(3, () -> {
            updatePillarObject(32864);
        });
        World.getWorld().unregisterNpc(this);
        npcs.remove(this);
    }
}
