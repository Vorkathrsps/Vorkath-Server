package com.aelous.model.content.raids.theatre.boss.maiden.blood;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.aelous.model.content.raids.theatre.boss.maiden.objects.BloodSplat;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.aelous.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

public class BloodSpawn extends NPC {
    Player player;
    MaidenProcess maiden;
    public List<BloodSpawn> orbList = new ArrayList<>();
    public List<BloodSplat> bloodObjectList = new ArrayList<>();
    public List<Integer> damage = new ArrayList<>();
    BloodSplat bloodSplat;
    TheatreArea theatreArea;

    public BloodSpawn(int id, Tile tile, Player player, MaidenProcess maiden, TheatreArea theatreArea) {
        super(id, tile);
        this.player = player;
        this.maiden = maiden;
        this.theatreArea = theatreArea;
        orbList.add(this);
        this.walkRadius(10);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    protected boolean verifyDamage() {
        Hit hit = player.hit(this, Utils.random(4, 8), 0, null);

        for (var o : bloodObjectList) {
            if (o.tile().equals(player.tile())) {
                hit.submit();
                addDamage(hit.getDamage());
                return true;
            }
        }
        return false;
    }

    public void heal() {
        Iterator<Integer> iterator = damage.iterator();
        while (iterator.hasNext()) {
            var d = iterator.next();
            maiden.healHit(this, d);
            iterator.remove();
        }
    }
    public void clearOrbAndObjects() {
        for (var o : bloodObjectList) {
            o.remove();
        }
        bloodObjectList.clear();
        for (var n : bloodOrbs) {
            World.getWorld().unregisterNpc(n);
        }
    }

    public void addDamage(int damageValue) {
        damage.add(damageValue);
    }

    @Override
    public void postSequence() {
        if (!orbList.isEmpty()) {
            bloodSplat = new BloodSplat(32984, new Tile(this.tile().getX(), this.tile().getY()).transform(0, 0, theatreArea.getzLevel()), 10, 0);
            if (!bloodObjectList.contains(bloodSplat)) {
                bloodObjectList.add(bloodSplat);
            }
            for (var o : bloodObjectList) {
                if (!ObjectManager.objWithTypeExists(10, new Tile(o.getX(), o.getY()).transform(0, 0, theatreArea.getzLevel()))) {
                    bloodSplat.spawn();
                }
            }
            verifyDamage();
            heal();
        } else {
            clearOrbAndObjects();
        }
    }

    @Override
    public void die() {
        orbList.remove(this);
        World.getWorld().unregisterNpc(this);
    }
}
