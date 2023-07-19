package com.aelous.model.content.raids.theatre.boss.xarpus.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.xarpus.objects.PoisonSplat;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class XarpusProcess extends NPC {
    Player player;

    @Getter @Setter private boolean entranceAnimationStarted = false;
    @Getter @Setter private boolean initiated = false;
    private int intervalCount = 0;
    private int splatInterval = 4;
    List<Tile> poisonTile = new ArrayList<>();
    List<GameObject> objects = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    public static final Area XARPUS_AREA = new Area(3177, 4394, 3163, 4380);

    public XarpusProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.setSize(5);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void sendPoisonPool() { //TODO add multiplayer support / projectile richochet
        var tile = player.tile().copy();
        this.faceTarget();
        Chain.noCtx().runFn(1, () -> {
            this.face(null);
        });
        this.animate(8059);
        var tileDist = this.tile().distance(tile);
        int duration = (68 + 25 + (10 * tileDist));
        var entityTile = this.tile().transform(1, 1, 0);
        Projectile p = new Projectile(entityTile, tile, 1555, 68, duration, 95, 0, 20, 5, 10);
        p.send(entityTile, tile);
        World.getWorld().tileGraphic(1556, tile, 0, p.getSpeed());
        PoisonSplat poisonSplat = new PoisonSplat(32744, p.getEnd(), 22, 0);
        Chain.noCtx().runFn((int) (p.getSpeed() / 30D), () -> {
            if (!poisonTile.contains(poisonSplat.tile()) && !objects.contains(poisonSplat)) {
                poisonTile.add(poisonSplat.tile());
                objects.add(poisonSplat);
                poisonSplat.spawn();
            }
        }).then(1, () -> poisonSplat.animate(8068));
    }

    public void setOpeningTransmog() {
        this.transmog(8340);
    }

    public void faceTarget() {
        this.face(player);
    }

    public void sendExhumed() { //TODO

    }

    Runnable entranceAnimation = () -> {
        this.lockNoDamage();
        this.animate(8061);
        Chain.noCtx().runFn(1, () -> {
            this.animate(8058);
        }).then(2, this::setOpeningTransmog).then(1, () -> {
            this.unlock();
            this.setInitiated(true);
        });
    };

    @Override
    public void postSequence() {
        if (!players.contains(player) && player.tile().withinArea(XARPUS_AREA)) {
            players.add(player);
        } else if (players.contains(player) && !player.tile().withinArea(XARPUS_AREA)) {
            players.remove(player);
        }
        if (this.dead() || !player.tile().withinArea(XARPUS_AREA)) {
            return;
        }

        if (player.tile().withinArea(XARPUS_AREA)) {
            if (!entranceAnimationStarted) {
                entranceAnimation.run();
                this.setEntranceAnimationStarted(true);
            }

            if (this.isInitiated()) {
                intervalCount++;
                splatInterval--;
                if (intervalCount >= 4 && splatInterval <= 0 && !this.dead()) {
                    sendPoisonPool();
                    intervalCount = 0;
                    splatInterval = 5;
                }
            }
        }
    }

    public void clear() {
        for (var o : objects) {
            o.remove();
            poisonTile.clear();
        }
        players.clear();
        this.setInitiated(false);
        this.setEntranceAnimationStarted(false);
    }

    @Override
    public void die() {
        Chain.noCtx().runFn(1, () -> {
            this.animate(8063);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        }).then(2, this::clear);
    }
}
