package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.aelous.model.World;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.*;
import java.util.stream.Collectors;

public class Vetion extends CommonCombatMethod {

    boolean canwalk = false;
    Set<Tile> usedTiles = new HashSet<>();
    Set<Tile> tiles = new HashSet<>(12);
    private final List<String> VETION_QUOTES = Arrays.asList("Dodge this!",
        "Sit still you rat!",
        "Die, rodent!", "I will end you!",
        "You can't escape!",
        "Filthy whelps!", "Time to die, mortal!", "You call that a weapon?!");

    private final List<String> VETION_QUOTES2 = Arrays.asList("Dodge this!",
        "Sit still you rat!",
        "Die, rodent!", "I will end you!",
        "You can't escape!",
        "Filthy whelps!");

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.face(null);
        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        var random = World.getWorld().random(5);
        switch (random) {
            case 0, 1 -> doMagicSwordRaise();
            case 2, 3 -> doMagicSwordSlash();
            case 4, 5 -> {
                if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
                    doShieldBash();
                }
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public void doFollowLogic() {
        NPC vetion = (NPC) entity;
        vetion.face(null);
        if (canwalk) {
            vetion.stepAbs(target.getX(), target.getY(), MovementQueue.StepType.REGULAR);
        }
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }

    public static final Area vetionArea = new Area(1879, 11559, 1895, 11533, 0); //block this when tb'd

    private void doMagicSwordRaise() {
        canwalk = true;
        NPC vetion = (NPC) entity;
        var transformedTile = target.tile().transform(3, 3, 0);
        List<Tile> tiles = transformedTile.area(3, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(transformedTile) && !ProjectileRoute.allow(target, pos));
        Tile destination = Utils.randomElement(tiles);
        if (tiles.isEmpty()) {
            return;
        }
        var lastTarget = target;
        for (int i = 0; i < 3; i++) {
            Tile finalDest = destination == null ? null : World.getWorld().randomTileAround(destination, i % 2 == 0 ? 2 : 3);
            if (finalDest != null && usedTiles.contains(finalDest)) {
                continue;
            }
            usedTiles.add(finalDest);
            Chain.noCtx().runFn(1, () -> {
                vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                vetion.lockMoveDamageOk();
                vetion.getMovementQueue().clear();
            }).runFn(1, () -> {
                vetion.animate(9969);
                vetion.graphic(vetion.id() == 6612 ? 2345 : 2344, GraphicHeight.MIDDLE, 0);
                if (finalDest != null) {
                    World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, finalDest, 0, 0);
                }
            }).then(3, () -> {
                if (target != null && target.isPlayer() && !target.dead() && entity.isRegistered() && !entity.dead()) {
                    if (destination != null && (target.tile().equals(finalDest))) {
                        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                        hit.setDamage(Utils.random(15, 30));
                        hit.submit();
                    }
                    if (finalDest != null && target.tile().inSqRadius(finalDest, 1) && !target.tile().equals(finalDest)) {
                        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                        hit.setDamage(hit.getDamage() / 2);
                        hit.submit();
                    }
                }
            }).then(3, () -> {
                vetion.unlock();
                vetion.getCombat().setTarget(lastTarget);
                vetion.face(null);
                canwalk = false;
                usedTiles.clear();
            });
        }
        var targetDest = new Tile(target.tile().x, target.tile().y, target.tile().level);
        Chain.bound(null).runFn(2, () -> World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, target.tile(), 0, 0)).then(3, () -> {
            if (target != null && target.isPlayer() && !target.dead() && target.isRegistered() && !entity.dead()) {
                if (destination != null && (target.tile().equals(targetDest))) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                    hit.setDamage(Utils.random(15, 30));
                    hit.submit();
                }
                if (target.tile().inSqRadius(targetDest, 1) && !target.tile().equals(targetDest)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                    hit.setDamage(hit.getDamage() / 2);
                    hit.submit();
                }
            }
        });
    }

    private void doMagicSwordSlash() {
        canwalk = true;
        NPC vetion = (NPC) entity;
        var transformedTile = target.tile().transform(3, 3, 0);
        List<Tile> tiles = transformedTile.area(3, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(transformedTile) && !ProjectileRoute.allow(target, pos));
        Tile destination = Utils.randomElement(tiles);
        if (tiles.isEmpty()) {
            return;
        }
        var lastTarget = target;
        for (int i = 0; i < 3; i++) {
            Tile finalDest = destination == null ? null : World.getWorld().randomTileAround(destination, i % 2 == 0 ? 2 : 3);
            if (finalDest != null && usedTiles.contains(finalDest)) {
                continue;
            }
            usedTiles.add(finalDest);
            Chain.noCtx().runFn(1, () -> {
                vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                vetion.lockMoveDamageOk();
                vetion.getMovementQueue().clear();
            }).runFn(1, () -> {
                vetion.animate(9972);
                if (finalDest != null) {
                    World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, finalDest, 0, 0);
                }
                if (vetion.id() == 6612) {
                    vetion.graphic(2348, GraphicHeight.LOW, 0);
                }
            }).then(3, () -> {
                if (target != null && target.isPlayer() && !target.dead() && target.isRegistered() && !entity.dead()) {
                    if (destination != null && (target.tile().equals(finalDest))) {
                        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                        hit.setDamage(Utils.random(15, 30));
                        hit.submit();
                    }
                    if (finalDest != null && target.tile().inSqRadius(finalDest, 1) && !target.tile().equals(finalDest)) {
                        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                        hit.setDamage(hit.getDamage() / 2);
                        hit.submit();
                    }
                }
            }).then(3, () -> {
                vetion.unlock();
                vetion.getCombat().setTarget(lastTarget);
                vetion.face(null);
                canwalk = false;
                usedTiles.clear();
            });
        }

        var targetDest = new Tile(target.tile().x, target.tile().y, target.tile().level);
        Chain.bound(null).runFn(2, () -> World.getWorld().tileGraphic(vetion.id() == 6612 ? 2347 : 2346, target.tile(), 0, 0)).then(3, () -> {
            if (target != null && target.isPlayer() && !target.dead() && target.isRegistered() && !entity.dead()) {
                if (destination != null && (target.tile().equals(targetDest))) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                    hit.setDamage(Utils.random(15, 30));
                    hit.submit();
                }
                if (target.tile().inSqRadius(targetDest, 1) && !target.tile().equals(targetDest)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).setAccurate(true);
                    hit.setDamage(hit.getDamage() / 2);
                    hit.submit();
                }
            }
        });
    }

    private void doShieldBash() {
        canwalk = true;
        NPC vetion = (NPC) entity;
        var lastTarget = target;
        vetion.waitUntil(() -> canwalk, () -> Chain.noCtx().runFn(1, () -> {
            vetion.forceChat(Utils.randomElement(VETION_QUOTES));
            vetion.setPositionToFace(target.tile());
            vetion.lockMoveDamageOk();
            vetion.getMovementQueue().clear();
            var dir = Direction.resolveForLargeNpc(lastTarget.tile(), entity.npc());
            spawnShieldInDir(this, entity.tile(), dir);
        }).runFn(3, () -> vetion.animate(9974)).then(2, () -> Chain.bound(null).cancelWhen(() -> !tiles.contains(target.tile())).then(1, () -> {
            if (target != null && target.isPlayer() && !target.dead() && target.isRegistered() && !entity.dead()) {
                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).setAccurate(true);
                hit.setDamage(Utils.random(15, 30));
                hit.submit();
            }
        })).then(4, () -> {
            vetion.unlock();
            vetion.getCombat().setTarget(lastTarget);
            vetion.face(null);
            canwalk = false;
            tiles.clear();
        }));
    }

    private void spawnHellhounds(NPC vetion, Entity target) {
        List<NPC> minions = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            VetionMinion minion = new VetionMinion(vetion, target);
            minions.add(minion);
            World.getWorld().registerNpc(minion);
        }

        vetion.animate(9976);
        vetion.forceChat(vetion.id() == 6611 ? "Gah! Hounds! get them!" : "HOUNDS! DISPOSE OF THESE TRESSPASSERS!");

        vetion.putAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED, true);
        vetion.putAttrib(AttributeKey.MINION_LIST, minions);
    }

    public boolean customOnDeath(Entity entity) {
        if (entity.isNpc()) {
            NPC purpleVetion = (NPC) entity;
            if ((purpleVetion.hp() == 0 || purpleVetion.dead()) && !purpleVetion.<Boolean>getAttribOr(AttributeKey.VETION_REBORN_ACTIVE, false)) {
                purpleVetion.heal(255); // Heal vetion
                purpleVetion.transmog(6612); //Transform into orange vetion
                purpleVetion.setTile(purpleVetion.tile());//Update tile
                purpleVetion.forceChat("Do it again!!");
                purpleVetion.getTimers().register(TimerKey.VETION_REBORN_TIMER, 500);
                purpleVetion.putAttrib(AttributeKey.VETION_REBORN_ACTIVE, true);
                return purpleVetion.transmog() != 6612;
            }
        }
        return false;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

    @Override
    public ArrayList<Entity> getPossibleTargets(Entity mob) {
        return Arrays.stream(mob.closePlayers(64)).collect(Collectors.toCollection(ArrayList::new));
    }


    public static void spawnShieldInDir(Vetion vetion, Tile origin, Direction dir) {
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
                vetion.tiles.add(pos); // you were missing side ones
                World.getWorld().tileGraphic(1448, pos, 0, 0);
                World.getWorld().tileGraphic(2349, pos, 0, 30);
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
            if (pattern.length == 0)
                return;
            for (Area area : pattern) {
                area.bottomLeft().showTempItem(995);
                area.topRight().showTempItem(995);
                area.forEachPos(t -> {
                    var pos = origin.transform(t.x, t.y);
                    vetion.tiles.add(pos);
                });
            }
            for (Tile tile : vetion.tiles) {
                World.getWorld().tileGraphic(1448, tile, 0, 0);
                World.getWorld().tileGraphic(2349, tile, 0, 30);
            }
        }
    }
}
