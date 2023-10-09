package com.cryptic.model.content.raids.theatre.boss.xarpus;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.xarpus.objects.PoisonSplat;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 7/18/2023
 */
public class Xarpus extends NPC {
    TheatreInstance theatreInstance;
    @Getter
    @Setter
    private boolean entranceAnimationStarted = false;
    @Getter
    @Setter
    private boolean initiated = false;
    private int intervalCount = 0;
    private int splatInterval = 4;
    List<Tile> poisonTile = new ArrayList<>();
    List<GameObject> objects = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    public static final Area XARPUS_AREA = new Area(3177, 4394, 3163, 4380);

    public Xarpus(int id, Tile tile, TheatreInstance theatreInstance) { //TODO add exhumed
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.setSize(5);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void sendPoisonPool() { //TODO add multiplayer support / projectile richochet
        var randomTarget = Utils.randomElement(theatreInstance.getPlayers());
        var tile = randomTarget.tile().copy();
        this.faceTarget(randomTarget);
        Chain.noCtx().runFn(1, () -> this.face(null));
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

    public void faceTarget(Player player) {
        this.face(player);
    }

    public void clear() {
        for (var o : objects) {
            o.remove();
            poisonTile.clear();
        }
        players.clear();
        this.splatInterval = 0;
        this.intervalCount = 0;
        this.setInitiated(false);
        this.setEntranceAnimationStarted(false);
    }

    @Override
    public void postSequence() {
        var randomTarget = Utils.randomElement(theatreInstance.getPlayers());

        if (randomTarget == null) {
            return;
        }

        Player player = null;

        for (var p : theatreInstance.getPlayers()) {
            if (p == null) {
                return;
            }
            player = p;
        }

        if (player == null) {
            return;
        }

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

            for (var t : poisonTile) {
                for (var p : theatreInstance.getPlayers()) {
                    var currentX = p.tile().getX();
                    var currentY = p.tile().getY();
                    var previousX = p.getPreviousTile().getX();
                    var previousY = p.getPreviousTile().getY();

                    var middleX = (currentX + previousX) / 2;
                    var middleY = (currentY + previousY) / 2;
                    if (t.equals(middleX, middleY) && !p.tile().equals(t) || t.equals(p.tile())) {
                        p.hit(this, Utils.random(4, 8));
                    }
                }
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

    @Override
    public void die() {
        players.clear();
        for (var p : theatreInstance.getPlayers()) {
            p.setRoomState(RoomState.COMPLETE);
            p.getTheatreInstance().onRoomStateChanged(p.getRoomState());
        }
        Chain.noCtx().runFn(1, () -> this.animate(8063)).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        }).then(2, this::clear);
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
}
