package com.aelous.model.entity.combat.method.impl.npcs.hydra;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;

import java.util.ArrayList;

import static com.aelous.model.entity.combat.method.impl.npcs.hydra.HydraChamber.*;

/**
 * The alchemical hydra NPC.
 *
 * @author Gabriel || Wolfsdarker
 */
public class AlchemicalHydra extends NPC {


    /**
     * The amount of attacks left until it changes.
     */
    public int recordedAttacks = 3;

    /**
     * The hydra's current phase.
     */
    public HydraPhase currentPhase = HydraPhase.GREEN;

    /**
     * The hydra's current attack.
     */
    public HydraAttacks currentAttack = HydraAttacks.MAGIC;

    /**
     * The moment of the last hydra's fire wall attack.
     */
    public long lastFirewall = 0L;

    /**
     * The moment of the last hydra's poison pool attack.
     */
    public long lastPoisonPool = 0L;

    /**
     * The moment of the last hydra's lightning attack.
     */
    public long lastLightning = 0L;

    /**
     * The hydra's instance base location
     */
    public Tile baseLocation;

    /**
     * If the hydra's shield is dropped.
     */
    private boolean shieldDropped = false;

    /**
     * The delay for the vent to show up
     */
    public int ventsDelay = 15;

    /**
     * The owner of this instanced hydra
     */
    public Player owner;

    public boolean firesActive;

    public boolean getShieldDropped() {
        return shieldDropped;
    }

    /**
     * Constructor for the hydra.
     */
    public AlchemicalHydra(Tile tile, Player owner) {
        super(8615, tile);
        putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN, 30);
        respawns(false);
        baseLocation = tile.transform(-hydraSpawnLoc.x, -hydraSpawnLoc.y, 0);
        this.owner = owner;
        animateVents(8280);//8280 is cancel animation
    }

    @Override
    public void postSequence() {

        if (locked()) {
            return;
        }

        ventsDelay--;

        if (ventsDelay == 5) {
            animateVents(8279);
        }

        if (ventsDelay == 3 && !isEnraged() && !shieldDropped) {
            var baseTile = tile().transform(-baseLocation.x, -baseLocation.y);

            if (isWithinRedVent(baseTile, getSize())) {
                if (currentPhase == HydraPhase.GREEN) {
                    forceChat("Roaaaaaaaaaaar!");
                    shieldDropped = true;
                } else if (currentPhase == HydraPhase.RED) {
                    owner.message("The chemicals are absorbed by the Alchemical Hydra; empowering it further!");
                }
            } else if (isWithinBlueVent(baseTile, getSize())) {
                if (currentPhase == HydraPhase.RED) {
                    forceChat("Roaaaaaaaaaaar!");
                    shieldDropped = true;
                } else if (currentPhase == HydraPhase.BLUE) {
                    owner.message("The chemicals are absorbed by the Alchemical Hydra; empowering it further!");
                }
            } else if (isWithinGreenVent(baseTile, getSize())) {
                if (currentPhase == HydraPhase.BLUE) {
                    forceChat("Roaaaaaaaaaaar!");
                    shieldDropped = true;
                } else if (currentPhase == HydraPhase.GREEN) {
                    owner.message("The chemicals are absorbed by the Alchemical Hydra; empowering it further!");
                }
            }

            if (shieldDropped) {
                owner.message("The chemicals neutralise the Alchemical Hydra's defence!");
            }
        }

        if (ventsDelay == 0) {
            animateVents(8280);
            ventsDelay = 20;
        }

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        if (healthAmount <= 0.75 && healthAmount > 0.50 && currentPhase == HydraPhase.GREEN) {
            changePhase();
        }
        if (healthAmount <= 0.50 && healthAmount > 0.25 && currentPhase == HydraPhase.BLUE) {
            changePhase();
        }
        if (healthAmount <= 0.25 && currentPhase == HydraPhase.RED) {
            changePhase();
        }
    }

    public boolean isEnraged() {
        return currentPhase == HydraPhase.ENRAGED;
    }

    /**
     * Returns the hydra's current attack anim based on its recorded attacks
     */
    public int getAttackAnim() {
        if(currentPhase == null) {
            return -1;//Phase is null don't write animations
        }
        switch (currentPhase) {
            case GREEN -> {
                return 8236;
            }
            case BLUE -> {
                return 8243;
            }
            case RED -> {
                return 8250;
            }
            case ENRAGED -> {
                return 8256;
            }
            default -> {
                return -1;//Should never get here
            }
        }
    }

    /**
     * Returns the hydra's next attack.
     */
    public HydraAttacks getNextAttack() {
        if (recordedAttacks > 0) {
            return currentAttack;
        }
        ArrayList<HydraAttacks> possibleAttacks = new ArrayList<>();

        for (HydraAttacks value : HydraAttacks.values()) {
            if (value.phaseRequired == null || value.phaseRequired == currentPhase) {
                if (value == HydraAttacks.FIRE_WALL) {
                    if ((System.currentTimeMillis() - lastFirewall > fireWallDelay)) {
                        possibleAttacks.add(value);
                    }
                } else if (value == HydraAttacks.POISON) {
                    if ((System.currentTimeMillis() - lastPoisonPool > poisonPoolDelay)) {
                        possibleAttacks.add(value);
                    }
                } else if (value == HydraAttacks.LIGHTNING) {
                    if ((System.currentTimeMillis() - lastLightning > lightningDelay)) {
                        possibleAttacks.add(value);
                    }
                } else {
                    possibleAttacks.add(value);
                }
            }
        }

        recordedAttacks = isEnraged() ? 1 : 3;
        /*possibleAttacks.remove(HydraAttacks.MAGIC); // testing
        possibleAttacks.remove(HydraAttacks.RANGED);
        possibleAttacks.remove(HydraAttacks.ENRAGED_POISON);*/

        var attack = possibleAttacks.get(Utils.rand(possibleAttacks.size() - 1));

        if (attack == HydraAttacks.POISON) {
            lastPoisonPool = System.currentTimeMillis();
        } else if (attack == HydraAttacks.FIRE_WALL) {
            lastFirewall = System.currentTimeMillis();
        } else if (attack == HydraAttacks.LIGHTNING) {
            lastLightning = System.currentTimeMillis();
        } else if (attack == HydraAttacks.RANGED && currentAttack == HydraAttacks.RANGED) {
            attack = HydraAttacks.MAGIC;
        } else if (attack == HydraAttacks.MAGIC && currentAttack == HydraAttacks.MAGIC) {
            attack = HydraAttacks.RANGED;
        }

        return attack;
    }

    /**
     * Changes the hydra's phase to the next phase
     */
    private void changePhase() {
        currentPhase.switchPhase(this);
        currentPhase = HydraPhase.values()[currentPhase.ordinal() + 1];

        if (currentPhase == HydraPhase.RED) {
            lastFirewall = 10000L;
        }
        shieldDropped = false;
    }

    /**
     * Animates the vent within the instance area.
     */
    private void animateVents(int animId) {
        for (GameObject vent : vents) {
            var instanceVent = MapObjects.get(vent.getId(), new Tile(baseLocation.transform(vent.tile()).x,
                baseLocation.transform(vent.tile()).y, 0));
            instanceVent.ifPresent(gameObject -> owner.getPacketSender().sendObjectAnimation(gameObject, animId));
        }
    }

}
