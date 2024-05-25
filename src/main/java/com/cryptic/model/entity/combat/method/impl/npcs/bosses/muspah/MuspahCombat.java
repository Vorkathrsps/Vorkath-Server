package com.cryptic.model.entity.combat.method.impl.npcs.bosses.muspah;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.muspah.instance.MuspahInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;

import java.util.*;

public class MuspahCombat extends CommonCombatMethod {

    boolean generated = false;

    @Override
    public void init(NPC npc) {
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 60);
        npc.getCombatInfo().aggroradius = 50;
        if (npc.id() == NpcIdentifiers.PHANTOM_MUSPAH_12078) {
            npc.putAttrib(AttributeKey.MUSPAH_MELEE, true);
        } else if (npc.id() == NpcIdentifiers.PHANTOM_MUSPAH) {
            npc.putAttrib(AttributeKey.MUSPAH_RANGE, true);
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        MuspahInstance instance = (MuspahInstance) target.getInstancedArea();
        if (instance != null) {
            spikes(instance);
        }
        return true;
    }

    void spikes(MuspahInstance instance) {
        if (generateInitialObjects(instance)) return;
        instance.spikeProgressionCount++;
        List<Tile> tiles = this.generatedTiles(instance);
        for (final Tile tile : tiles) {
            instance.setSpikes(tile);
        }
    }

    List<Tile> generatedTiles(MuspahInstance instance) {
        List<Tile> temp = new ArrayList<>();
        int count = 0;
        List<GameObject> entries = new ArrayList<>(instance.getSpikes());
        Tile ownerTile = instance.getOwner().tile().copy();
        temp.add(ownerTile);
        for (var object : entries) {
            if (count >= instance.spikeProgressionCount + 1) break;
            Tile tile = object.tile();
            Tile transform = this.getTransform(tile, instance);
            if (transform != null) {
                temp.add(transform);
                count++;
            }
        }
        return temp;
    }

    Tile getTransform(Tile tile, MuspahInstance instance) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        Collections.shuffle(Arrays.asList(directions));
        for (int[] dir : directions) {
            int newX = tile.getX() + dir[0];
            int newY = tile.getY() + dir[1];
            Tile next = new Tile(newX, newY, instance.getzLevel());
            if (!RegionManager.zarosBlock(next) && next.allowObjectPlacement()) return next;
        }
        return null;
    }

    List<Tile> generateTiles(MuspahInstance instance) {
        List<Tile> temp = new ArrayList<>();
        temp.add(instance.getOwner().tile().clone());
        while (temp.size() < instance.spikeProgressionCount) {
            Tile tile = instance.getRoom().randomTile();
            if (!RegionManager.zarosBlock(tile) && tile.allowObjectPlacement()) {
                temp.add(tile);
            }
        }
        return temp;
    }

    boolean generateInitialObjects(MuspahInstance instance) {
        if (!generated) {
            List<Tile> tileList = this.generateTiles(instance);
            for (final Tile tile : tileList) {
                instance.setSpikes(tile);
            }
            this.generated = true;
            return true;
        }
        return false;
    }

    private void melee(final MuspahInstance instance) {
        final Player target = instance.getOwner();
        this.entity.animate(9920);
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 5;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        NPC npc = (NPC) this.entity;
        return npc.id() == NpcIdentifiers.PHANTOM_MUSPAH_12078 ? 1 : 6;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
