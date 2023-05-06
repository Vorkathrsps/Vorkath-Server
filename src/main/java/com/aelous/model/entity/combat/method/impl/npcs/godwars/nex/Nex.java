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
import com.aelous.model.phase.Phase;
import com.aelous.model.phase.PhaseStage;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Getter
    public Phase phase = new Phase(PhaseStage.ONE);
    public AtomicBoolean progressingPhase = new AtomicBoolean(false);

    public AtomicBoolean darkenScreen = new AtomicBoolean(false);
    public long lastNoEscape;
    public long lastSiphon;
    public boolean doingSiphon;
    public boolean soulsplit;

    public boolean turmoil;

    public final List<GameObject> stalagmite = new ArrayList<>();
    public boolean stalagmiteDestroyed;
    public ArrayList<NPC> bloodReavers = new ArrayList<>();

    public ArrayList<Entity> calculatePossibleTargets(Tile current, Tile tile, boolean northSouth) {
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity e : getCombatMethod().getPossibleTargets(this)) {
            var a1 = new Area(current.getX(), current.getY(), tile.getX() + (northSouth ? 2 : 0), tile.getY() + (!northSouth ? 2 : 0));
            var a2 = new Area(tile.getX(), tile.getY(), current.getX() + (northSouth ? 2 : 0), current.getY() + (!northSouth ? 2 : 0));
            if (e.tile().inArea(a1)
                || e.tile().inArea(a2)) {
                list.add(e);
            }
        }
        return list;
    }

    public void progressNextPhase() {
        if (phase.getStage() == PhaseStage.ONE && bodyguardPhase == BodyguardPhase.FUMUS) {
            bodyguardPhase = null;
            Chain.bound(null).runFn(1, () -> {
                progressingPhase.getAndSet(true);
                stopActions(true);
            }).then(2, () -> {
                forceChat("Darken my shadow!");
                darkenScreen.getAndSet(true);
            }).then(3, () -> {
                phase.setStage(PhaseStage.TWO);
                getCombat().delayAttack(1);
                progressingPhase.getAndSet(false);
            });
        } else if (phase.getStage() == PhaseStage.TWO && bodyguardPhase == BodyguardPhase.UMBRA) {
            bodyguardPhase = null;
            Chain.bound(null).runFn(1, () -> {
                progressingPhase.getAndSet(true);
                stopActions(true);
            }).then(2, () -> {
                forceChat("Flood my lungs with blood!");
            }).then(3, () -> {
                phase.setStage(PhaseStage.THREE);
                getCombat().delayAttack(1);
                progressingPhase.getAndSet(false);
            });
        } else if (phase.getStage() == PhaseStage.THREE && bodyguardPhase == BodyguardPhase.CRUOR) {
            bodyguardPhase = null;
            Chain.bound(null).runFn(1, () -> {
                progressingPhase.getAndSet(true);
                stopActions(true);
                killBloodReavers();
            }).then(2, () -> {
                forceChat("Infuse me with the power of ice!");
            }).then(3, () -> {
                phase.setStage(PhaseStage.FOUR);
                getCombat().delayAttack(1);
                progressingPhase.getAndSet(false);
            });
        } else if (phase.getStage() == PhaseStage.FOUR && bodyguardPhase == BodyguardPhase.GLACIES) {
            bodyguardPhase = null;
            Chain.bound(null).runFn(1, () -> {
                progressingPhase.getAndSet(true);
                stopActions(true);
            }).then(2, () -> {
                forceChat("NOW, THE POWER OF ZAROS!");
                animate(9179);
                graphic(2016);
            }).then(3, () -> {
                phase.setStage(PhaseStage.FIVE);
                getCombat().delayAttack(1);
                healHit(this, 500);
                progressingPhase.getAndSet(false);
            });
        }
    }

    public void killBloodReavers() {
        if (bloodReavers == null) {
            return;
        }
        for (NPC npc : bloodReavers) {
            if (npc == null) {
                continue;
            }
            if (!npc.dead()) {
                npc.hit(this, npc.hp());
                if (!this.dead()) {
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
        if (phase.getStage() == PhaseStage.FIVE) {
            if (World.getWorld().getTickCount() % 41 == 0) {
                if (soulsplit) {
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
        if (doingSiphon)
            return;
        if (this.progressingPhase.get())
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
