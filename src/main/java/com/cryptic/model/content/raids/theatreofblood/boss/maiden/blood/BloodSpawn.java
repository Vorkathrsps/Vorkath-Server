package com.cryptic.model.content.raids.theatreofblood.boss.maiden.blood;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatreofblood.TheatreInstance;
import com.cryptic.model.content.raids.theatreofblood.boss.maiden.Maiden;
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

public class BloodSpawn extends NPC {
    Player player;
    Maiden maiden;
    List<Integer> damage = new ArrayList<>();
    TheatreInstance theatreInstance;

    public BloodSpawn(int id, Tile tile, Player player, Maiden maiden, TheatreInstance theatreInstance) {
        super(id, tile);
        this.player = player;
        this.maiden = maiden;
        this.theatreInstance = theatreInstance;
        this.theatreInstance.orbList.add(this);
        this.walkRadius(10);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    protected boolean verifyDamage() {
        Hit hit = player.hit(this, Utils.random(4, 8), 0, null);
        for (var o : this.theatreInstance.bloodObjectList) {
            if (o == null) continue;
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
        for (var o : this.theatreInstance.bloodObjectList) {
            if (o == null) continue;
            o.remove();
        }
        this.theatreInstance.bloodObjectList.clear();
        for (var n : this.theatreInstance.orbList) {
            if (n == null) continue;
            World.getWorld().unregisterNpc(n);
        }
    }

    public void addDamage(int damageValue) {
        damage.add(damageValue);
    }

    @Override
    public void combatSequence() {
        if (this.maiden.dead()) {
            for (var o : this.theatreInstance.bloodObjectList) {
                if (o == null) continue;
                o.remove();
            }
            for (var n : this.theatreInstance.orbList) {
                if (n == null) continue;
                n.remove();
            }
            this.damage.clear();
            this.theatreInstance.orbList.clear();
            this.theatreInstance.bloodObjectList.clear();
            return;
        }

        if (!this.theatreInstance.orbList.isEmpty()) {
            GameObject bloodSplat = new GameObject(32984, new Tile(this.tile().getX(), this.tile().getY(), theatreInstance.getzLevel()), 10, 0);
            if (!ObjectManager.objWithTypeExists(10, new Tile(bloodSplat.getX(), bloodSplat.getY(), theatreInstance.getzLevel()))) {
                this.theatreInstance.bloodObjectList.add(bloodSplat);
            }
            for (var o : this.theatreInstance.bloodObjectList) {
                if (!ObjectManager.objWithTypeExists(10, new Tile(o.getX(), o.getY(), theatreInstance.getzLevel()))) {
                    if (this.theatreInstance.bloodObjectList.contains(bloodSplat)) {
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
        this.theatreInstance.orbList.remove(this);
        World.getWorld().unregisterNpc(this);
    }
}
