package com.aelous.model.entity.combat.method.impl.npcs.verzik;

import com.aelous.model.entity.combat.method.impl.npcs.hydra.HydraPhase;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.entity.combat.method.impl.npcs.hydra.HydraChamber.hydraSpawnLoc;
import static com.aelous.model.entity.combat.method.impl.npcs.verzik.VerzikRoom.*;

/**
 * The hydra's current phase.
 */

public class VerzikVitur extends NPC {

    public Player owner;

    public VerzikVitur(Tile tile, Player owner) {
        super(8372, tile);
        respawns(false);
        baseLocation = tile.transform(-verzikP1SpawnLocation.x, -verzikP1SpawnLocation.y, 0);
        this.owner = owner;
    }

    /**
     * The Verziks instance base location
     */
    public Tile baseLocation;

    @Override
    public void sequence() {
        super.sequence();

        if (locked()) {
            return;
        }

        if (dead()) {
            return;
        }

    }


    public VerzikPhase currentPhase = VerzikPhase.P0;
    private void changePhase() {
        currentPhase.switchPhase(this);
        currentPhase = VerzikPhase.values()[currentPhase.ordinal() + 1];
    }
}
