package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.cryptic.model.World;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.phase.Phase;
import com.cryptic.model.phase.PhaseStage;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

public class VetionCombat extends CommonCombatMethod {
    boolean hasAttacked = false;
    Set<Tile> usedTiles = new HashSet<>();
    public static List<Player> playersInArea = new ArrayList<>();
    @Getter
    @Setter
    public Phase phase = new Phase(PhaseStage.ONE);
    public static final Area vetionArea = new Area(1877, 11534, 1897, 11555, 1); //block this when tb'd
    Set<Tile> tiles = new HashSet<>(12);
    private final List<String> VETION_QUOTES = Arrays.asList("Dodge this!",
        "Sit still you rat!",
        "Die, rodent!", "I will end you!",
        "You can't escape!",
        "Filthy whelps!", "Time to die, mortal!", "You call that a weapon?!", "Now i've got you!");

    @Override
    public void preDefend(Hit hit) {
        Player player = (Player) target;
        NPC vetion = (NPC) entity;
        if (player != null) {
            if (houndList.size() == 0) {
                vetion.message("My hounds! I'll make you pay for that!");
            }
            if (!hasAttacked) {
                vetion.getCombat().setTarget(player);
            }
        }
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.face(null);

        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        if (Utils.percentageChance(50)) {
            magicSwordRaise();
        } else if (Utils.percentageChance(50)) {
            magicSwordSlash();
        } else {
            if (withinDistance(5)) {
                doShieldBash();
            }
        }

        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public void doFollowLogic() {
        NPC vetion = (NPC) entity;
        vetion.face(null);
        if (hasAttacked) {
            var t = new Tile(target.tile().getX(), target.tile().getY()).transform(1, 1);
            vetion.stepAbs(t.getX(), t.getY(), MovementQueue.StepType.REGULAR);
        }
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

    void magicSwordRaise() {
        NPC vetion = (NPC) entity;
        for (var player : getPossibleTargets(vetion)) {
            if (player == null) continue;
            List<Tile> tileList = new ArrayList<>();
            var targetTile = player.tile();
            tileList.add(targetTile);
            usedTiles.add(targetTile);
            for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
                var tilesAround = World.getWorld().randomTileAround(targetTile, 4);
                if (tilesAround.getZ() != target.getZ()) continue;
                if (usedTiles.contains(tilesAround)) continue;
                usedTiles.add(tilesAround);
                if (tileList.contains(tilesAround)) continue;
                tileList.add(tilesAround);
            }
            tileList
                .stream()
                .filter(tile -> World.getWorld().clipAt(tile.x, tile.y, tile.level) == 0)
                .filter(tile -> !tile.equals(vetion.tile()))
                .forEach(tile ->
                    Chain.noCtx()
                        .runFn(1, () -> {
                            vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                            vetion.lockMoveDamageOk();
                            vetion.getMovementQueue().clear();
                        })
                        .then(1, () -> {
                            vetion.animate(9969);
                            vetion.graphic(vetion.id() == 6612 ? 2345 : 2344, GraphicHeight.MIDDLE, 0);
                            World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, tile, 0, 0);
                        })
                        .then(3, () -> {
                            if (!player.tile().equals(tile) && !player.tile().inSqRadius(tile, 1)) {
                                return;
                            }
                            if (!player.dead() && player.isRegistered() && !vetion.dead() && player.tile().equals(tile)) {
                                vetion.submitAccurateHit(player, 0, Utils.random(15, 30), this);
                            } else if (player.tile().nextTo(tile)) {
                                vetion.submitAccurateHit(player, 0, (Utils.random(15, 30) / 2), this);
                            }
                        })
                        .then(2, () -> {
                            vetion.unlock();
                            vetion.getCombat().setTarget(player);
                            vetion.face(null);
                            hasAttacked = true;
                            usedTiles.clear();
                            tiles.clear();
                        }));
        }
    }

    void magicSwordSlash() {
        NPC vetion = (NPC) entity;
        for (var player : getPossibleTargets(vetion)) {
            if (player == null) continue;
            List<Tile> tileList = new ArrayList<>();
            var targetTile = player.tile();
            tileList.add(targetTile);
            usedTiles.add(targetTile);
            for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
                var tilesAround = World.getWorld().randomTileAround(targetTile, 4);
                if (tilesAround.getZ() != target.getZ()) continue;
                if (usedTiles.contains(tilesAround)) continue;
                usedTiles.add(tilesAround);
                if (tileList.contains(tilesAround)) continue;
                tileList.add(tilesAround);
            }
            tileList
                .stream()
                .filter(tile -> World.getWorld().clipAt(tile.x, tile.y, tile.level) == 0)
                .filter(tile -> !tile.equals(vetion.tile()))
                .forEach(tile ->
                    Chain.noCtx().runFn(1, () -> {
                            vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                            vetion.lockMoveDamageOk();
                            vetion.getMovementQueue().clear();
                        })
                        .then(1, () -> {
                            vetion.animate(9972);
                            vetion.graphic(2348);
                            World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, tile, 0, 0);
                        })
                        .then(3, () -> {
                            if (!player.tile().equals(tile) && !player.tile().inSqRadius(tile, 1)) {
                                return;
                            }
                            if (!player.dead() && player.isRegistered() && !vetion.dead() && player.tile().equals(tile)) {
                                vetion.submitAccurateHit(player, 0, Utils.random(15, 30), this);
                            } else if (player.tile().nextTo(tile)) {
                                vetion.submitAccurateHit(player, 0, (Utils.random(15, 30) / 2), this);
                            }
                        })
                        .then(2, () -> {
                            vetion.unlock();
                            vetion.getCombat().setTarget(player);
                            vetion.face(null);
                            hasAttacked = true;
                            usedTiles.clear();
                            tiles.clear();
                        }));
        }
    }

    private void doShieldBash() {
        NPC vetion = (NPC) entity;
        Player player = (Player) target;
        Chain.noCtx().runFn(1, () -> {
            vetion.forceChat(Utils.randomElement(VETION_QUOTES));
            vetion.setPositionToFace(player.tile());
            vetion.lockMoveDamageOk();
            vetion.getMovementQueue().clear();
            var dir = Direction.resolveForLargeNpc(player.tile(), vetion.npc());
            spawnShieldInDir(this, vetion.tile(), dir);
        }).then(2, () -> vetion.animate(9974)).then(1, () -> {
            for (var t : tiles) {
                if (t.equals(player.tile())) {
                    if (!player.dead() && !vetion.dead()) {
                        vetion.submitAccurateHit(player, 0, Utils.random(15, 30), this);
                    }
                }
            }
        }).then(2, () -> {
            vetion.unlock();
            vetion.getCombat().setTarget(player);
            vetion.face(null);
            hasAttacked = true;
            tiles.clear();
        });
    }

    List<NPC> houndList = Lists.newArrayList();

    private void spawnHellhounds(NPC vetion, Entity target) {
        for (int index = 0; index < 2; index++) {
            var tilesAround = World.getWorld().randomTileAround(vetion.tile(), 4);
            if (!MovementQueue.dumbReachable(tilesAround.x, tilesAround.y, vetion.tile())) continue;
            VetionMinion minion = new VetionMinion(vetion, target, tilesAround);
            houndList.add(minion);
            minion.setIgnoreOccupiedTiles(true);
            minion.spawn();
            minion.face(target);
            minion.getCombat().setTarget(target);
        }

        vetion.animate(9976);
        vetion.forceChat(vetion.id() == 6611 ? "Gah! Hounds! get them!" : "HOUNDS! DISPOSE OF THESE TRESSPASSERS!");

        vetion.putAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED, true);
        vetion.putAttrib(AttributeKey.MINION_LIST, houndList);
    }

    @Override
    public void onRespawn(NPC npc) {
        npc.spawnDirection(Direction.SOUTH.toInteger());
        npc.getCombat().setTarget(null);
        npc.canAttack(false);
        npc.transmog(6611, false);
        Chain.noCtx().runFn(1, () -> {
            npc.canAttack(true);
            npc.getCombat().setTarget(target);
        });
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        if (entity instanceof NPC npc) {
            if (npc.id() == 6611) {
                Chain.noCtx().runFn(1, () -> {
                    npc.lockNoDamage();
                    npc.canAttack(false);
                    npc.message("Now.. DO IT AGAIN!!!");
                    npc.transmog(6612, true);
                    npc.animate(9979);
                    npc.getTimers().register(TimerKey.VETION_REBORN_TIMER, 500);
                    npc.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, true);
                }).then(1, () -> {
                    npc.unlock();
                    npc.canAttack(true);
                });
            } else if (npc.id() == 6612) {
                for (var m : Lists.newArrayList(houndList)) {
                    if (m == null) continue;
                    houndList.remove(m);
                    m.die();
                }
                houndList.clear();
                npc.getTimers().cancel(TimerKey.VETION_REBORN_TIMER);
                npc.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, false);
                npc.clearAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED);
                npc.die();
            }
        }
        return true;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public ArrayList<Entity> getPossibleTargets(Entity entity) {
        return Arrays.stream(entity.closePlayers(64)).filter(t -> vetionArea.contains(t.tile())).collect(Collectors.toCollection(ArrayList::new));
    }


    public static void spawnShieldInDir(VetionCombat vetionCombat, Tile origin, Direction dir) {
        var PATTERNS = new int[][][]{ // player is [NESW] of vetion
            // NESW
            new int[][]{
                new int[]{-2, 4}, new int[]{-1, 4}, new int[]{0, 4}, new int[]{1, 4}, new int[]{2, 4}, new int[]{3, 4}, new int[]{4, 4}
                , new int[]{-1, 3}, new int[]{0, 3}, new int[]{1, 3}, new int[]{2, 3}, new int[]{3, 3}
            },
            new int[][]{
                new int[]{4, 4}, new int[]{4, 3}, new int[]{4, 2}, new int[]{4, 1}, new int[]{4, 0}, new int[]{4, -1}, new int[]{4, -2}
                , new int[]{3, 3}, new int[]{3, 2}, new int[]{3, 1}, new int[]{3, 0}, new int[]{3, -1}
            },
            new int[][]{
                new int[]{-2, -2}, new int[]{-1, -2}, new int[]{0, -2}, new int[]{1, -2}, new int[]{2, -2}, new int[]{3, -2}, new int[]{4, -2}
                , new int[]{-1, -1}, new int[]{0, -1}, new int[]{1, -1}, new int[]{2, -1}, new int[]{3, -1}
            },
            new int[][]{
                new int[]{-2, -2}, new int[]{-2, -1}, new int[]{-2, 0}, new int[]{-2, 1}, new int[]{-2, 2}, new int[]{-2, 3}, new int[]{-2, 4}
                , new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1}, new int[]{-1, 2}, new int[]{-1, 3}
            }
        };
        if (dir.ordinal() <= 3) {
            int[][] pattern = PATTERNS[dir.ordinal()];
            if (pattern.length == 0)
                return;
            for (int[] offset : pattern) {
                if (offset == null || offset.length == 0)
                    break;
                var pos = origin.transform(offset[0], offset[1]);
                vetionCombat.tiles.add(pos); // you were missing side ones
                World.getWorld().tileGraphic(1446, pos, 0, 0);
                World.getWorld().tileGraphic(2184, pos, 0, 90);
                //World.getWorld().tileGraphic(2349, pos, 0, 30);
            }
        } else if (dir.ordinal() >= 4 && dir.ordinal() <= 7) {
            Area[][] PATTERNS2 = new Area[][]{
                new Area[]{new Area(-2, 1, -1, 4), new Area(-2, 3, 1, 4),},
                new Area[]{new Area(3, 1, 4, 4), new Area(1, 3, 4, 4),},
                new Area[]{new Area(1, -2, 4, -1), new Area(3, -2, 4, 1),},
                new Area[]{new Area(-2, -2, -1, 1), new Area(-2, -2, 1, -1),},
            };
            Area[] pattern = PATTERNS2[dir.ordinal() - 4];
            if (pattern.length == 0) return;
            for (Area area : pattern) {
                area.forEachPos(t -> {
                    var pos = origin.transform(t.x, t.y);
                    vetionCombat.tiles.add(pos);
                });
            }
            for (Tile tile : vetionCombat.tiles) {
                World.getWorld().tileGraphic(2236, tile, 0, 0);
                World.getWorld().tileGraphic(2184, tile, 0, 90);
            }
        }
    }
}
