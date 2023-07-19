package com.aelous.model.content.raids.theatre.boss.maiden.blood;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.maiden.objects.BloodSplat;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aelous.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

public class BloodSpawn extends NPC {
    AtomicInteger count = new AtomicInteger(0);
    Player player;
    public List<Tile> bloodTiles = new ArrayList<>();
    public List<GameObject> bloodObjects = new ArrayList<>(); //TODO figure out why tiles are rendering dead after new bloodspawn is made
    public List<Integer> damage = new ArrayList<>();

    public void addDamage(int damageValue) {
        damage.add(damageValue);
    }

    public BloodSpawn(int id, Tile tile, Player player) {
        super(id, tile);
        spawns.add(this);
        this.player = player;
        this.walkRadius(4);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    public void clear() {
        this.die();
    }

    @Override
    public void postSequence() {
        BloodSplat bloodSplat = new BloodSplat(32984, new Tile(this.tile().getX(), this.tile().getY(), this.tile().getZ()), 10, 0);
        if (!bloodObjects.contains(bloodSplat)) {
            bloodObjects.add(bloodSplat);
            bloodSplat.spawn();
        }

        if (!bloodTiles.contains(bloodSplat.tile()) && !bloodSplat.tile().equals(this.tile())) {
            bloodTiles.add(bloodSplat.tile());
        }
        Hit hit = player.hit(this, Utils.random(4, 8), 0, null);
        for (var o : bloodObjects) {
            if (o.tile().equals(player.tile())) {
                hit.submit();
                this.addDamage(hit.getDamage());
                break;
            }
        }
    }

    @Override
    public void die() {
        for (var o : bloodObjects) {
            o.remove();
            bloodTiles.remove(o.tile());
            System.out.println(bloodTiles.remove(o.tile()));
        }
        bloodObjects.clear();
        for (var n : spawns) {
            World.getWorld().unregisterNpc(n);
            bloodTiles.remove(n.tile());
        }
        spawns.clear();
        bloodTiles.clear();
        World.getWorld().unregisterNpc(this);
    }


}
