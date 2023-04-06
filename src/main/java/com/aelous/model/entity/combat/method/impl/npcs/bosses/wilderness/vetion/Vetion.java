package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness.vetion;

import com.aelous.model.World;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;
import com.mysql.cj.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Vetion extends CommonCombatMethod {

    boolean canwalk = false;
    boolean hasWalked = false;
    Set<Tile> usedTiles = new HashSet<>();
    private final List<String> VETION_QUOTES = Arrays.asList("Dodge this!",
        "Sit still you rat!",
        "Die, rodent!",
        "You can't escape!",
        "Filthy whelps!");

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.face(null);
        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        var random = World.getWorld().random(3);
        if (hasWalked) {
            switch (random) {
                case 0, 1 -> doMagicSwordRaise();
                case 2, 3 -> doMagicSwordSlash();
            }
        }
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
        hasWalked = false;
        NPC vetion = (NPC) entity;
        var transformedTile = target.tile().transform(3, 3, 0);
        List<Tile> tiles = transformedTile.area(3, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(transformedTile) && !ProjectileRoute.allow(target, pos));
        Tile destination = Utils.randomElement(tiles);
        if (tiles.isEmpty()) {
            return;
        }
        var lastTarget = target;
        for (int i = 0; i < 4; i++) {
            Tile finalDest = destination == null ? null : World.getWorld().randomTileAround(destination, i % 2 == 0 ? 2 : 3);
            if (finalDest != null && usedTiles.contains(finalDest)) {
                continue;
            }
            usedTiles.add(finalDest);
            vetion.waitUntil(() -> canwalk, () -> Chain.noCtx().runFn(1, () -> {
                vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                vetion.lockMoveDamageOk();
                vetion.getMovementQueue().clear();
            }).runFn(1, () -> {
                vetion.animate(9969);
                vetion.graphic(2344, GraphicHeight.MIDDLE, 0);
                if (finalDest != null) {
                    World.getWorld().tileGraphic(2346, finalDest, 0, 0);
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
                hasWalked = true;
                usedTiles.clear();
            }));
        }
    }

    private void doMagicSwordSlash() {
        canwalk = true;
        hasWalked = false;
        NPC vetion = (NPC) entity;
        var transformedTile = target.tile().transform(3, 3, 0);
        List<Tile> tiles = transformedTile.area(3, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(transformedTile) && !ProjectileRoute.allow(target, pos));
        Tile destination = Utils.randomElement(tiles);
        if (tiles.isEmpty()) {
            return;
        }
        var lastTarget = target;
        for (int i = 0; i < 4; i++) {
            Tile finalDest = destination == null ? null : World.getWorld().randomTileAround(destination, i % 2 == 0 ? 2 : 3);
            if (finalDest != null && usedTiles.contains(finalDest)) {
                continue;
            }
            usedTiles.add(finalDest);
            vetion.waitUntil(() -> canwalk, () -> Chain.noCtx().runFn(1, () -> {
                vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                vetion.lockMoveDamageOk();
                vetion.getMovementQueue().clear();
            }).runFn(1, () -> {
                vetion.animate(9972);
                if (finalDest != null) {
                    World.getWorld().tileGraphic(2346, finalDest, 0, 0);
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
                hasWalked = true;
                usedTiles.clear();
            }));
        }
    }

    private void doShieldBash() {
        canwalk = true;
        NPC vetion = (NPC) entity;
        var transformedTile = target.tile().transform(3, 3, 0);
        List<Tile> tiles = transformedTile.area(5, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(transformedTile) && !ProjectileRoute.allow(target, pos));
        Tile destination = Utils.randomElement(tiles);
        if (tiles.isEmpty()) {
            return;
        }
        var lastTarget = target;
        for (int i = 0; i < 10; i++) {
            Tile finalDest = destination == null ? null : World.getWorld().randomTileAround(destination, i % 2 == 0 ? 2 : 3);
            vetion.waitUntil(() -> canwalk, () -> Chain.noCtx().runFn(1, () -> {
                vetion.forceChat(Utils.randomElement(VETION_QUOTES));
                vetion.face(target);
                vetion.lockMoveDamageOk();
                vetion.getMovementQueue().clear();
            }).runFn(1, () -> {
                vetion.animate(9974);
                if (finalDest != null) {
                    World.getWorld().tileGraphic(2349, finalDest, 0, 0);
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
            }));
        }
    }

    private void spawnHellhounds(NPC vetion, Entity target) {
        List<NPC> minions = new ArrayList<>();
        for (int index = 0; index < 2; index++) {
            VetionMinion minion = new VetionMinion(vetion, target);
            minions.add(minion);
            World.getWorld().registerNpc(minion);
        }

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

}
