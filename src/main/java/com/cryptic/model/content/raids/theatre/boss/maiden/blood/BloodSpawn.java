package com.cryptic.model.content.raids.theatre.boss.maiden.blood;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.maiden.Maiden;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.cryptic.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

public class BloodSpawn extends NPC {
    Player player;
    Maiden maiden;
    List<BloodSpawn> orbList;
    List<GameObject> bloodObjectList;
    List<Integer> damage = new ArrayList<>();
    TheatreInstance theatreInstance;

    public BloodSpawn(int id, Tile tile, Player player, Maiden maiden, TheatreInstance theatreInstance) {
        super(id, tile);
        this.player = player;
        this.maiden = maiden;
        this.theatreInstance = theatreInstance;
        this.bloodObjectList = new ArrayList<>();
        this.orbList = new ArrayList<>();
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
    public void postCombatProcess() {
        if (this.maiden.dead()) {
            for (var o : this.bloodObjectList) {
                o.remove();
            }
            for (var n : this.orbList) {
                n.remove();
            }
            this.damage.clear();
            this.orbList.clear();
            this.bloodObjectList.clear();
            return;
        }

        if (!this.orbList.isEmpty()) {
            GameObject bloodSplat = new GameObject(32984, new Tile(this.tile().getX(), this.tile().getY(), theatreInstance.getzLevel()), 10, 0);
            if (!ObjectManager.objWithTypeExists(10, new Tile(bloodSplat.getX(), bloodSplat.getY(), theatreInstance.getzLevel()))) {
                this.bloodObjectList.add(bloodSplat);
            }
            for (var o : this.bloodObjectList) {
                if (!ObjectManager.objWithTypeExists(10, new Tile(o.getX(), o.getY(), theatreInstance.getzLevel()))) {
                    if (this.bloodObjectList.contains(bloodSplat)) {
                        bloodSplat.spawn();
                    }
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
