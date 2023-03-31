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
            scaleNPC(this, partySize);
    }

    public RaidsNpc(int id, Tile tile, Direction direction, int partySize, boolean scale) {
        super(id, tile);
        this.respawns(false);
        if (scale)
            scaleNPC(this, partySize);
        this.spawnDirection(direction.toInteger());
    }

    public RaidsNpc(int id, Tile tile, int partySize, boolean scale) {
        super(id, tile);
        this.respawns(false);
        if (scale)
            scaleNPC(this, partySize);
    }

    private void scaleNPC(NPC npc, int partySize) {
        if (npc.getCombatMethod() == null) {
            return;
        }
        double factor;
        factor = 1.1 + (0.20 * partySize);
        if (factor != 0 & partySize > 1) {
            var newHp = (int) (npc.hp() * factor);
            npc.setHitpoints(newHp); // scale stats. note that this also scales the hp on top of the per-player bonus already added above. as these are both multiplicative modifiers it does no matter which one is applied first
            npc.getCombatInfo().stats.hitpoints = newHp;
        }
    }
}
