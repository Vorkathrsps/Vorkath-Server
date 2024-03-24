package com.cryptic.model.content.raids.tombsofamascut.warden.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

public class KephriPhantomCombat extends CommonCombatMethod {
    TombsInstance instance;

    @Override
    public void init(NPC npc) {
        this.instance = npc.getTombsInstance();
    }

    @Override
    public void doFollowLogic() {
        this.entity.setEntityInteraction(null);
        this.entity.face(null);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC kephri = (NPC) entity;
        if (!this.instance.initiatedPhaseOne) return false;
        if (this.instance.isWardenDead) {
            kephri.remove();
            return false;
        }
        attack(kephri);
        return true;
    }

    void attack(NPC npc) {
        npc.animate(9577);
        final Tile baseTile = new Tile(3928, 5156, this.instance.getzLevel() + 1);
        final List<Tile> tiles = new ArrayList<>();
        final List<Tile> projectileTiles = new ArrayList<>();
        for (var p : this.instance.getPlayers()) {
            final Tile pLocation = p.tile();
            if (!projectileTiles.contains(pLocation)) {
                projectileTiles.add(pLocation);
            }
            if (!tiles.contains(pLocation)) {
                tiles.add(pLocation);
            }
        }

        for (var tile : tiles) {
            if (tile == null) continue;
            World.getWorld().tileGraphic(1447, tile, 0, 38);
        }

        for (var tile : projectileTiles) {
            if (tile == null) continue;
            double distance = tile.distanceTo(tile);
            int duration = (int) (31 + (82 + distance));
            Projectile projectile = new Projectile(baseTile, tile, 1481, 31, duration, 39, 50, 51, 0, 0);
            projectile.send(baseTile, tile);
            World.getWorld().tileGraphic(2157, tile, 0, projectile.getSpeed());
        }

        Chain.noCtx().runFn(3, () -> {
        });
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 7;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
