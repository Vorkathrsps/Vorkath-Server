package com.aelous.model.content.raids;

import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

/**
 * An NPC with raids attributes.
 *
 * @author Patrick van Elderen | May, 10, 2021, 16:16
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class RaidsNpc extends NPC {

    public static final double BONUS_HP_PER_PLAYER = 0.40; // %increased hp for each players beyond the first

    public RaidsNpc(int id, Tile tile, int partySize) {
        super(id, tile);
        this.respawns(false);
        if (this.combatInfo() != null)
            this.combatInfo().aggroradius = 15;
        this.walkRadius(15);
        this.setHitpoints((int) (this.hp() * (1 + (BONUS_HP_PER_PLAYER * (partySize - 1)))));
    }

    public RaidsNpc(int id, Tile tile, Direction direction, int partySize, boolean scale) {
        super(id, tile);
        this.respawns(false);
        if (this.combatInfo() != null)
            this.combatInfo().aggroradius = 15;
        this.walkRadius(15);
        if (scale)
            this.setHitpoints((int) (this.hp() * (1 + (BONUS_HP_PER_PLAYER * (partySize - 1)))));
        this.spawnDirection(direction.toInteger());
    }
}
