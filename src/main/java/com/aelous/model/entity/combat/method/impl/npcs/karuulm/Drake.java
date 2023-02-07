package com.aelous.model.entity.combat.method.impl.npcs.karuulm;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

/**
 * @author Patrick van Elderen | December, 22, 2020, 14:56
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Drake extends NPC {

    /**
     * The amount of attacks left until the volcanic breath.
     */
    public int recordedAttacks = 7;

    public Drake(int id, Tile tile) {
        super(id, tile);
    }
}
