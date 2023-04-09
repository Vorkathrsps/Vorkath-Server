package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCDeath;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.region.Region;
import com.aelous.model.map.region.RegionManager;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PVE
 * @Since augustus 07, 2020
 */
public class Nechryael extends CommonCombatMethod {

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (Utils.rollDie(4, 1))
            spawnDeathSpawns(entity, target);
        basicAttack(entity, target);
        return true;
    }

    public void onDeath(Entity entity) {
        List<NPC> minions = entity.getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
        for (NPC deathSpawn : minions) {
            if (!deathSpawn.hidden()) {
                NPCDeath.deathReset(deathSpawn);
                deathSpawn.animate(deathSpawn.getCombatInfo().animations.death);
                deathSpawn.respawns(false);
            }
        }

        Chain.bound(null).name("DeathSpawnTask").runFn(2, () -> {
            for (NPC deathSpawn : minions) {
                deathSpawn.hidden(true);
                World.getWorld().unregisterNpc(deathSpawn);
            }
        });
    }

    private void spawnDeathSpawns(Entity entity, Entity target) {
        List<NPC> minions = entity.getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
        for (NPC deathSpawn : minions) {
            if(deathSpawn.isRegistered()) {
                return;
            }
        }
        for (int i = 0; i < 2; i++) {
            Tile pos = getSpawnPosition(entity, target);
            if (pos == null) {
                continue;
            }
            NPC spawn = new NPC(NpcIdentifiers.DEATH_SPAWN, pos);
            World.getWorld().registerNpc(spawn);
            spawn.putAttrib(AttributeKey.BOSS_OWNER, entity);

            List<NPC> list = entity.getAttribOr(AttributeKey.MINION_LIST, new ArrayList<NPC>());
            list.add(spawn);
            entity.putAttrib(AttributeKey.MINION_LIST, list);
            spawn.getCombat().setTarget(target);
            spawn.setPositionToFace(target.tile());
        }
    }

    private Tile getSpawnPosition(Entity entity, Entity target) {
        List<Tile> tiles = new ArrayList<>(18);
        int radius = 1;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                Region tile = RegionManager.getRegion(target.getX() + x, target.getY() + y);
                if (tile == null || ProjectileRoute.allow(target, target.getX() + x, target.getY() + y)) {
                    tiles.add(new Tile(target.getX() + x, target.getY(), target.getZ()));
                }
            }
        }
        if (tiles.size() == 0) {
            return null;
        }
        return Utils.randomElement(tiles);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
