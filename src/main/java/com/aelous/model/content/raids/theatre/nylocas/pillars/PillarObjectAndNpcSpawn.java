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
    @Getter
    GameObject gameObject;
    @Nonnull
    Player player;

    public PillarObjectAndNpcSpawn(int id, Tile tile, GameObject gameObject, @NotNull Player player, VasiliasListener vasiliasListener) {
        super(id, tile);
        this.gameObject = gameObject;
        this.player = player;
        this.setSize(4);
        vasiliasListener.pillarNpc.add(this);
        vasiliasListener.pillarObject.add(gameObject);
    }

    public void clearPillarNpcsAndObjects() {
        VasiliasListener vasiliasListener = new VasiliasListener(player);
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
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    public void spawnPillarObject() {
        getGameObject().spawn();
    }

    public void updatePillarObject(int newID) {
        MapObjects.get(this.getGameObject().getId(), this.getGameObject().tile()).ifPresent(o -> {
            o.setId(newID);
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
        VasiliasListener vasiliasListener = new VasiliasListener(player);
        MapObjects.get(gameObject.getId(), gameObject.tile()).ifPresent(o -> {
            vasiliasListener.pillarNpc.remove(this);
            o.animate(8074);
            Chain.noCtx().delay(4, () -> {
                this.die();
                World.getWorld().unregisterNpc(this);
                o.setId(32864);
            });
        });
    }

}
