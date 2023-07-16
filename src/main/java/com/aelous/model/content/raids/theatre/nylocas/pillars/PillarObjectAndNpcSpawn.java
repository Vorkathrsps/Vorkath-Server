package com.aelous.model.content.raids.theatre.nylocas.pillars;

import com.aelous.model.World;

import com.aelous.model.content.raids.theatre.nylocas.VasiliasListener;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class PillarObjectAndNpcSpawn extends NPC {
    @Nonnull
    @Getter
    GameObject gameObject;
    @Nonnull
    Player player;
    VasiliasListener vasiliasListener;

    public PillarObjectAndNpcSpawn(int id, Tile tile, @NotNull GameObject gameObject, @NotNull Player player, VasiliasListener vasiliasListener) {
        super(id, tile);
        this.gameObject = gameObject;
        this.player = player;
        vasiliasListener.pillarNpc.add(this);
        vasiliasListener.pillarObject.add(gameObject);
    }

    public void clearPillarNpcsAndObjects() {
        for (var v : vasiliasListener.pillarObject) {
            v.setId(32862);
        }
        vasiliasListener.pillarObject.clear();
        for (var n : vasiliasListener.pillarNpc) {
            n.remove();
        }
        vasiliasListener.vasiliasNpc.clear();
    }

    public void spawnPillarNpc() {
        this.spawn(false);
        World.getWorld().registerNpc(this);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    public void spawnPillarObject() {
        getGameObject().spawn();
    }

    public void updatePillarObject(int newID) {
        MapObjects.get(getGameObject().getId(), getGameObject().tile()).ifPresent(o -> {
            o.setId(newID);
            this.gameObject = o;
            vasiliasListener.pillarObject.add(o);
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
        vasiliasListener.pillarNpc.remove(this);
    }

}
