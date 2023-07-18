package com.aelous.model.content.raids.theatre.boss.xarpus.handler;

import com.aelous.model.World;
import com.aelous.model.entity.combat.method.impl.npcs.hydra.HydraChamber;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

public class XarpusProcess extends NPC {
    Player player;
    private int intervalCount = 0;
    private int splatInterval = 5;

    public XarpusProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.setSize(5);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void faceTarget() {
        this.face(player);
    }

    public static int getPoolGraphic(NPC hydra, Tile targetTile) {
        var center = Utils.getCenterLocation(hydra);
        var dir = Direction.of(targetTile.x - center.x, targetTile.y - center.y);
        for (HydraChamber.PoisonPools pool : HydraChamber.PoisonPools.values()) {
            if (pool.offsetX == dir.x() && pool.offsetZ == dir.y()) {
                return pool.graphicId;
            }
        }
        return 1654;
    }

    public void sendPoisonPool() {
        var tileDist = this.tile().distance(player.tile());
        int duration = (50 + -5 + (10 * tileDist));
        Projectile p = new Projectile(this, player.tile(), 1644, 50, duration, 105, 0, 0, 5, 10);
        p.send(this, player.tile());
        World.getWorld().tileGraphic(1645, player.tile(), 0, p.getSpeed());
        var graphicId = getPoolGraphic(this, player.tile());
        World.getWorld().tileGraphic(graphicId, player.tile(), 0, p.getSpeed());


        Projectile p2 = new Projectile(p.getEnd(), player.tile(), 1644, 50, p.getSpeed() * 2, 0, 0, 80, 5, 10);
        p2.send(p2.getStart(), player.tile());
        World.getWorld().tileGraphic(1645, player.tile(), 0, p2.getSpeed());
        World.getWorld().tileGraphic(graphicId, player.tile(), 0, p2.getSpeed());
    }

    public void sendPoisonFromPoisonTile() {

    }

    @Override
    public void postSequence() {
        intervalCount++;
        if (intervalCount >= splatInterval) {
            sendPoisonPool();
        }

    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }
}
