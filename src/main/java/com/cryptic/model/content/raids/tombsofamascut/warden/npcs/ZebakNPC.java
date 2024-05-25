package com.cryptic.model.content.raids.tombsofamascut.warden.npcs;

import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.content.raids.tombsofamascut.warden.combat.ZebakPhantomCombat;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;

public class ZebakNPC extends NPC {
    @Getter
    TombsInstance tombsInstance;
    public ZebakNPC(int id, Tile tile, int spawnDirection, TombsInstance tombsInstance) {
        super(id, tile, spawnDirection);
        this.tombsInstance = tombsInstance;
        this.setCombatMethod(new ZebakPhantomCombat());
        this.getMovementQueue().setBlockMovement(true);
    }
}
