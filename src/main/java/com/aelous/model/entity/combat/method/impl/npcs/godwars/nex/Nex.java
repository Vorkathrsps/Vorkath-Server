package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.NEX_11280;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.NEX_11281;
import static com.aelous.model.entity.combat.method.impl.npcs.godwars.nex.NexFightState.*;

/**
 * The nex
 *
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 13, 2022
 */
public class Nex extends NPC {

    public Nex(int id, Tile tile) {
        super(id, tile);
        useSmartPath = true;
    }

    public BodyguardPhase bodyguardPhase = null;

    public NexFightState fightState = SMOKE_PHASE;
    public long lastNoEscape;
    public long lastSiphon;
    public boolean doingSiphon;
    public boolean soulsplit; // An overhead prayer in which Nex will heal a small percentage of her health for every successful hit she inflicts on up to three players.

    public boolean turmoil;

    public final List<GameObject> stalagmite = new ArrayList<>();
    public boolean stalagmiteDestroyed;
    public ArrayList<NPC> bloodReavers = new ArrayList<>();

    public ArrayList<Entity> calculatePossibleTargets(Tile current, Tile tile, boolean northSouth) {
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity e : getCombatMethod().getPossibleTargets(this)) {
            var a1 = new Area(current.getX(), current.getY(), tile.getX() + (northSouth ? 2 : 0), tile.getY() + (!northSouth ? 2 : 0));
            var a2 = new Area(tile.getX(), tile.getY(), current.getX() + (northSouth ? 2 : 0), current.getY() + (!northSouth ? 2 : 0));
            // a1.forEachPos(t -> t.showTempItem());
            // a2.forEachPos(t -> t.showTempItem());
            if (e.tile().inArea(a1)
                || e.tile().inArea(a2)) {
                list.add(e);
            }
        }
        return list;
    }

    public void progressNextPhase() {
        if (fightState == SMOKE_PHASE && bodyguardPhase == BodyguardPhase.FUMUS) {
            fightState = SHADOW_PHASE;
            forceChat("Darken my shadow!");
            bodyguardPhase = null;
        } else if (fightState == SHADOW_PHASE && bodyguardPhase == BodyguardPhase.UMBRA) {
            fightState = BLOOD_PHASE;
            forceChat("Flood my lungs with blood!");
            bodyguardPhase = null;
        } else if (fightState == BLOOD_PHASE && bodyguardPhase == BodyguardPhase.CRUOR) {
            fightState = ICE_PHASE;
            forceChat("Infuse me with the power of ice!");
            bodyguardPhase = null;
            killBloodReavers();
        } else if (fightState == ICE_PHASE && bodyguardPhase == BodyguardPhase.GLACIES) {
            fightState = ZAROS_PHASE;
            forceChat("NOW, THE POWER OF ZAROS!");
            bodyguardPhase = null;
            animate(9179);
            getCombat().delayAttack(1);
            healHit(this,500);
        }
    }

    public void killBloodReavers() {
        if(bloodReavers == null) {
            return;
        }
        for (NPC npc : bloodReavers) {
            if(npc == null) {
                continue;
            }
            if(!npc.dead()) {
                npc.hit(this, npc.hp());
                if(!this.dead()) {
                    this.healHit(this, npc.hp());
                }
            }
        }
    }

    @Override
    public NexCombat getCombatMethod() {
        return (NexCombat) super.getCombatMethod();
    }

    @Override
    public void postSequence() {

        if (fightState == ZAROS_PHASE) {
            if(World.getWorld().getTickCount() % 41 == 0) {
                if(soulsplit) {
                    transmog(NEX_11280);
                    soulsplit = false;
                } else {
                    transmog(NEX_11281);
                    soulsplit = true;
                }
            }
        }
    }

    @Override
    public void animate(int animation) {
        if (doingSiphon) // dont override until this anim over
            return;
        super.animate(animation);
    }

    @Override
    public void graphic(int graphic) {
        if (doingSiphon) // dont override until this anim over
            return;
        super.graphic(graphic);
    }
}
