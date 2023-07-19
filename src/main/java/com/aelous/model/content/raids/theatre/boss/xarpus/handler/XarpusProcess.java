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

import java.util.ArrayList;
import java.util.List;

public class XarpusProcess extends NPC {
    Player player;
    private int intervalCount = 0;
    private int splatInterval = 4;
    List<Tile> poisonTile = new ArrayList<>();
    List<GameObject> objects = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    public static final Area XARPUS_AREA = new Area(3177, 4394, 3163, 4380);
    int HEALING_XARPUS = 10767;

    public XarpusProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.setSize(5);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void setHealingPhase() {
        this.transmog(HEALING_XARPUS);
    }

    public void faceTarget() {
        this.face(player);
    }

    public void sendPoisonPool() { //TODO add multiplayer support / projectile hopping
        var tile = player.tile().copy();
        this.faceTarget();
        Chain.noCtx().runFn(1, () -> {
            this.face(null);
        });
        this.animate(8059);
        var tileDist = this.tile().distance(tile);
        int duration = (68 + 25 + (10 * tileDist));
        Projectile p = new Projectile(this, tile, 1555, 68, duration, 95, 0, 20, 5, 10);
        p.send(this, tile);
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

    public void sendExhumed() { //TODO

    }

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
        intervalCount++;
        splatInterval--;
        if (intervalCount >= 4 && splatInterval <= 0 && !this.dead()) {
            sendPoisonPool();
            intervalCount = 0;
            splatInterval = 5;
        }
    }

    @Override
    public void die() {
        Chain.noCtx().runFn(1, () -> {
            this.animate(8063);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        }).then(2, () -> {
            for (var o : objects) {
                o.remove();
                poisonTile.clear();
            }
        });
    }
}
