package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.NEX_11280;

/**
 * The nex
 *
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 13, 2022
 */
public class Nex extends NPC {

    private int stage;
    private int minionStage;
    private int flyTime;
    private long lastNoEscape;
    private long lastVirus;
    private long lastSiphon;
    private boolean doingSiphon;
    private final NPC[] bloodReavers;
    private int switchPrayersDelay;
    private boolean turmoilAttackUsed;

    public boolean isTurmoilAttackUsed() {
        return turmoilAttackUsed;
    }

    public void setTurmoilAttackUsed(boolean turmoilAttackUsed) {
        this.turmoilAttackUsed = turmoilAttackUsed;
    }

    public Nex(int id, Tile tile) {
        super(id, tile);
        cantInteract(true);
        bloodReavers = new NPC[3];
    }

    @Override
    public void sequence() {
        if (flyTime > 0)
            flyTime--;

        if (getStage() == 0 && minionStage == 0 && hp() <= 2720) {
            capDamage(0);
            forceChat("Fumus, don't fail me!");
            getCombat().delayAttack(1);
            ZarosGodwars.breakFumusBarrier();
            minionStage = 1;
        } else if (getStage() == 1 && minionStage == 1 && hp() <= 2040) {
            capDamage(0);
            forceChat("Umbra, don't fail me!");
            getCombat().delayAttack(1);
            ZarosGodwars.breakUmbraBarrier();
            minionStage = 2;
        } else if (getStage() == 2 && minionStage == 2 && hp() <= 1360) {
            capDamage(0);
            forceChat("Cruor, don't fail me!");
            getCombat().delayAttack(1);
            ZarosGodwars.breakCruorBarrier();
            minionStage = 3;
        } else if (getStage() == 3 && minionStage == 3 && hp() <= 680) {
            capDamage(0);
            forceChat("Glacies, don't fail me!");
            getCombat().delayAttack(1);
            ZarosGodwars.breakGlaciesBarrier();
            minionStage = 4;
        } else if (getStage() == 4 && minionStage == 4) {
            if (switchPrayersDelay > 0)
                switchPrayersDelay--;
            else {
                switchPrayers();
                resetSwitchPrayersDelay();
            }
        }

        if (dead() || doingSiphon || cantInteract()) {
            return;
        }
    }

    public ArrayList<Entity> calculatePossibleTargets(Tile current, Tile position, boolean northSouth) {
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity e : ZarosGodwars.getPossibleTargets()) {
            if (e.tile().inArea(current.getX(), current.getY(), position.getX() + (northSouth ? 2 : 0),
                position.getY() + (!northSouth ? 2 : 0))

                || e.tile().inArea(position.getX(), position.getY(), current.getX() + (northSouth ? 2 : 0),
                current.getY() + (!northSouth ? 2 : 0)))
                list.add(e);
        }
        return list;
    }

    public void moveNextStage() {
        if (getStage() == 0 && minionStage == 1) {
            forceChat("Darken my shadow!");
            Projectile projectile = new Projectile(ZarosGodwars.umbra, this, 2010, 30, 60, 18, 18, 0);
            projectile.sendProjectile();
            getCombat().delayAttack(1);
            setStage(1);
            capDamage(-1);
        } else if (getStage() == 1 && minionStage == 2) {
            forceChat("Flood my lungs with blood!");
            Projectile projectile = new Projectile(ZarosGodwars.cruor, this, 2010, 30, 60, 18, 18, 0);
            projectile.sendProjectile();
            getCombat().delayAttack(1);
            setStage(2);
            capDamage(-1);
        } else if (getStage() == 2 && minionStage == 3) {
            killBloodReavers();
            forceChat("Infuse me with the power of ice!");
            Projectile projectile = new Projectile(ZarosGodwars.glacies, this, 2010, 30, 60, 18, 18, 0);
            projectile.sendProjectile();
            getCombat().delayAttack(1);
            setStage(3);
            capDamage(-1);
        } else if (getStage() == 3 && minionStage == 4) {
            forceChat("NOW, THE POWER OF ZAROS!");
            animate(9179);
            getCombat().delayAttack(1);
            heal(500);
            setStage(4);
            capDamage(-1);
        }
    }

    public void resetSwitchPrayersDelay() {
        switchPrayersDelay = 35; // 25sec
    }

    public void switchPrayers() {
        transmog(NEX_11280);
    }

    @Override
    public void onHit(Hit hit) {
        if (hit.getTarget().isNpc()) {
            if (doingSiphon)
                this.hit(this, hit.getDamage(), SplatType.NPC_HEALING_HITSPLAT);

            if (id() == NEX_11280 && hit.getCombatType() == CombatType.MELEE) {
                Entity source = hit.getAttacker();
                if (source != null) {
                    int deflectedDamage = (int) (hit.getDamage() * 0.1);
                    if (deflectedDamage > 0)
                        hit.getAttacker().hit(hit.getTarget(), deflectedDamage, 1, null).setIsReflected().submit();
                }
            }
        }
        super.onHit(hit);
    }

    @Override
    public void animate(int animation) {
        if (doingSiphon)
            return;
        super.animate(animation);
    }

    @Override
    public void graphic(int graphic) {
        if (doingSiphon)
            return;
        super.graphic(graphic);
    }

    public int getAttacksStage() {
        return getStage();
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(int flyTime) {
        this.flyTime = flyTime;
    }

    public long getLastVirus() {
        return lastVirus;
    }

    public void setLastVirus(long lastVirus) {
        this.lastVirus = lastVirus;
    }

    public long getLastNoEscape() {
        return lastNoEscape;
    }

    public void setLastNoEscape(long lastNoEscape) {
        this.lastNoEscape = lastNoEscape;
    }

    public long getLastSiphon() {
        return lastSiphon;
    }

    public void setLastSiphon(long lastSiphon) {
        this.lastSiphon = lastSiphon;
    }

    public NPC[] getBloodReavers() {
        return bloodReavers;
    }

    public void killBloodReavers() {
        for (int index = 0; index < bloodReavers.length; index++) {
            if (bloodReavers[index] == null)
                continue;
            NPC npc = bloodReavers[index];
            bloodReavers[index] = null;
            if (npc.dead())
                return;
            this.hit(this, npc.hp(), SplatType.NPC_HEALING_HITSPLAT);
            npc.hit(npc, npc.hp());
        }
    }

    public void clearInstance(Player player) {

    }

    public void setDoingSiphon(boolean doingSiphon) {
        this.doingSiphon = doingSiphon;
    }

    public int getStage() {
        return stage;
    }
}
