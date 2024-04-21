package com.cryptic.model.entity.combat.method.impl.npcs.bosses.xamphur;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.ItemDrops;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class XamphurCombat extends CommonCombatMethod {
    boolean initiated = false;
    List<GameObject> objects = new ArrayList<>();
    Set<Tile> tiles = new HashSet<>();
    List<Entity> corruption = new ArrayList<>();
    Map<Player, Integer> damageMap = new HashMap<>();

    @Override
    public void init(NPC npc) {
        buildMarksOfDarknessTiles();
        Chain.noCtx().runFn(2, () -> {
            npc.transmog(10953, true);
            npc.animate(9060);
            npc.lock();
            Chain.noCtx().runFn(4, () -> {
                npc.animate(-1);
                npc.transmog(10954, true);
                npc.animate(9062);
            }).then(4, () -> {
                npc.animate(Animation.DEFAULT_RESET_ANIMATION);
                npc.transmog(10955, true);
                npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
                for (var object : objects) object.spawn();
                npc.unlock();
            }).then(1, () -> this.initiated = true);
        });
    }

    @Override
    public void process(Entity entity, Entity target) {
        for (var player : getPossibleTargets(this.entity)) {
            if (player == null || player.dead()) continue;
            if (player.hasAttrib(AttributeKey.MARK_OF_DARKNESS)) continue;
            for (var tile : tiles) {
                if (tile == null) continue;
                if (tile.equals(player.tile())) markOfDarkness(player);
            }
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!this.initiated) return false;
        if (Utils.rollDie(25, 1)) resetAndShuffleMarks();
        if (Utils.rollDie(2, 1)) regularAttack();
        else handAttack();
        return true;
    }

    final void regularAttack() {
        entity.animate(9064);
        for (var player : getPossibleTargets(this.entity)) {
            if (player == null || player.dead()) continue;
            player.graphic(1915);
            entity.submitHit(player, 0, Utils.random(0, 5), HitMark.REGULAR);
        }
    }


    final void handAttack() {
        entity.animate(9065);
        for (var player : getPossibleTargets(this.entity)) {
            if (player == null || player.dead()) continue;
            Tile firstLocation = player.tile().copy();
            World.getWorld().tileGraphic(1919, firstLocation, 0, 15);
            Chain.bound(player).runFn(2, () -> {
                if (player.tile().equals(firstLocation)) {
                    entity.submitHit(player, 0, Utils.random(0, 5), HitMark.REGULAR);
                }
            });
        }
        Chain.noCtx().runFn(2, () -> entity.animate(Animation.DEFAULT_RESET_ANIMATION));
    }

    final void markOfDarkness(Entity player) {
        if (player == null || player.dead()) return;
        if (player.hasAttrib(AttributeKey.MARK_OF_DARKNESS)) return;
        player.message("<col=a53fff>A Mark of Darkness has been placed upon you.</col>");
        player.sendPrivateSound(5015);
        player.graphic(1852);
        player.putAttrib(AttributeKey.MARK_OF_DARKNESS, true);
        int[] taskCount = new int[]{0};
        int[] hitCount = new int[]{0};
        corruption.add(player);
        Chain.noCtxRepeat().repeatingTask(1, task -> {
            if (taskCount[0] >= 50 || !player.hasAttrib(AttributeKey.MARK_OF_DARKNESS)) {
                removeFromList(player);
                player.sendPrivateSound(5000);
                player.message("<col=6800bf>Your Mark of Darkness has faded away.</col>");
                player.clearAttrib(AttributeKey.MARK_OF_DARKNESS);
                task.stop();
                return;
            }
            if (hitCount[0] >= 4) {
                hitCount[0] = 0;
                entity.submitHit(target, 0, Utils.random(1, 5), HitMark.CORRUPTION);
            }
            taskCount[0]++;
            hitCount[0]++;
        });
    }

    final void removeFromList(Entity player) {
        for (var p : Lists.newArrayList(corruption.iterator())) {
            if (!p.equals(player)) continue;
            corruption.remove(p);
        }
    }

    final void buildMarksOfDarknessTiles() {
        for (int index = 0; index < 16; index++) {
            var randomTile = World.getWorld().randomTileAround(this.entity.tile(), 6);
            if (randomTile.allowObjectPlacement() && MovementQueue.dumbReachable(randomTile.getX(), randomTile.getY(), entity.tile())) {
                tiles.add(randomTile);
            }
        }
        for (var tile : tiles) objects.add(new GameObject(41881, tile));
    }

    final void resetAndShuffleMarks() {
        for (var object : objects) object.remove();
        objects.clear();
        tiles.clear();
        buildMarksOfDarknessTiles();
        for (var object : objects) object.spawn();
    }

    @Override
    public void postDamage(Hit hit) {
        var target = hit.getTarget();
        var source = hit.getSource();
        if (source instanceof Player player && target instanceof NPC) {
            if (!damageMap.containsKey(player)) damageMap.put(player, hit.getDamage());
            else damageMap.computeIfPresent(player, (_, v) -> v + hit.getDamage());
        }
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        if (this.entity instanceof NPC npc) {
            for (var player : corruption) {
                if (!player.hasAttrib(AttributeKey.MARK_OF_DARKNESS)) continue;
                player.clearAttrib(AttributeKey.MARK_OF_DARKNESS);
            }
            tiles.clear();
            ItemDrops drops = new ItemDrops();
            Chain.noCtx().runFn(3, () -> {
                computeDropTable(npc, drops);
                clear(npc);
            });
        }
        return true;
    }

    final void clear(NPC npc) {
        for (var object : objects) object.remove();
        damageMap.clear();
        objects.clear();
        npc.remove();
    }

    final void computeDropTable(NPC npc, ItemDrops drops) {
        for (var entry : damageMap.entrySet()) {
            var player = entry.getKey();
            var damage = entry.getValue();
            if (player == null || damage < 100) continue;
            drops.rollTheDropTable(player, npc);
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 64;
    }
}
