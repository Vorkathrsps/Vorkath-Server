package com.cryptic.model.entity.combat.method.impl.npcs.karuulm;

import com.cryptic.model.entity.combat.method.impl.npcs.hydra.HydraAttacks;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | December, 22, 2020, 17:58
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Hydra extends NPC {

    public Hydra(int id, Tile tile) {
        super(id, tile);
    }

    /**
     * The amount of attacks left until it changes.
     */
    public int recordedAttacks = 3;

    /**
     * The hydra's current attack.
     */
    public HydraAttacks currentAttack = HydraAttacks.MAGIC;

    /**
     * The moment of the last hydra's poison pool attack.
     */
    public long lastPoisonPool = System.currentTimeMillis();

    @Override
    public void takeHit() {
        super.takeHit();

        if (System.currentTimeMillis() - lastPoisonPool > 60000L) {
            lastPoisonPool = System.currentTimeMillis();
        }
    }
}
