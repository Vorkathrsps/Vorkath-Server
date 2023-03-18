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
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;
import com.mysql.cj.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Vetion extends CommonCombatMethod {

    boolean canwalk = false;
    int walkCount = 0;

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        walkCount++;
        entity.face(null);
        if (entity.hp() <= 125 && !entity.hasAttrib(AttributeKey.VETION_HELLHOUND_SPAWNED)) {
            spawnHellhounds((NPC) entity, target);
        }

        // doMagicSwordSlash();
        var random = World.getWorld().random(10);
       switch (random) {
           case 0, 1 -> doMagicSwordRaise();
           case 2, 3 -> doMagicSwordSlash();
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
        return 6;
    }

    private void doMagicSwordRaise() {
        canwalk = false; //he doesnt follow, only faces & paths
        // ok when this attack happens what should happen.. he walk to the tile? when does it lock? , he locks to avoid interaction, then he steps to tile, then when at tile, performs attack, without updating facing
        NPC vetion = (NPC) entity;
        vetion.lockMoveDamageOk();// here let me show u how he interacts
        List<Tile> tiles = entity.tile().area(1, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(entity.tile()) && !ProjectileRoute.allow(entity, pos));
        Tile destination = Utils.randomElement(tiles);
        Tile finalDest1 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest2 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest3 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest4 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest5 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest6 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        if (destination == null)
            return;
        var lastTarget = target;
        Chain.noCtx().runFn(1, () -> {
            vetion.forceChat("Dodge this!");
            vetion.setPositionToFace(target.tile());
        }).runFn(1, () -> {
            vetion.animate(9969);
            vetion.graphic(2344, GraphicHeight.MIDDLE, 0);
            World.getWorld().tileGraphic(2346, finalDest1, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest2, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest3, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest4, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest5, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest6, 0, 30);
        }).then(2, () -> {
            if (target != null && target.isPlayer() && !target.dead() && entity.isRegistered() && !entity.dead()) {
                if (target.tile().equals(finalDest1) || target.tile().equals(finalDest2) || target.tile().equals(finalDest3) || target.tile().equals(finalDest4) || target.tile().equals(finalDest5) || target.tile().equals(finalDest6)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                } else if (target.tile().isWithinDistance(finalDest1, 1) || target.tile().isWithinDistance(finalDest2, 1) || target.tile().isWithinDistance(finalDest3, 1) || target.tile().isWithinDistance(finalDest4, 1) || target.tile().isWithinDistance(finalDest5, 1) || target.tile().isWithinDistance(finalDest6, 1)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                    hit.setDamage(hit.getDamage() / 2);
                }
            }
        }).then(2, () -> {
            vetion.unlock();
            vetion.getCombat().setTarget(lastTarget);
            entity.face(null);
        });
    }

    private void doMagicSwordSlash() {
        canwalk = false;
        NPC vetion = (NPC) entity;
        vetion.lockMoveDamageOk();// here let me show u how he interacts
        List<Tile> tiles = entity.tile().area(1, pos -> World.getWorld().clipAt(pos.x, pos.y, pos.level) == 0 && !pos.equals(entity.tile()) && !ProjectileRoute.allow(entity, pos));
        Tile destination = Utils.randomElement(tiles);
        Tile finalDest1 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest2 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest3 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest4 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest5 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        Tile finalDest6 = destination == null ? null : World.getWorld().randomTileAround(destination, 4);
        if (destination == null)
            return;
        var lastTarget = target;
        Chain.noCtx().runFn(1, () -> {
            vetion.forceChat("stfu");
            vetion.setPositionToFace(target.tile());
        }).runFn(1, () -> {
            vetion.animate(9972);
            World.getWorld().tileGraphic(2346, finalDest1, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest2, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest3, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest4, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest5, 0, 30);
            World.getWorld().tileGraphic(2346, finalDest6, 0, 30);
        }).then(2, () -> {
            if (target != null && target.isPlayer() && !target.dead() && entity.isRegistered() && !entity.dead()) {
                if (target.tile().equals(finalDest1) || target.tile().equals(finalDest2) || target.tile().equals(finalDest3) || target.tile().equals(finalDest4) || target.tile().equals(finalDest5) || target.tile().equals(finalDest6)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                } else if (target.tile().isWithinDistance(finalDest1, 1) || target.tile().isWithinDistance(finalDest2, 1) || target.tile().isWithinDistance(finalDest3, 1) || target.tile().isWithinDistance(finalDest4, 1) || target.tile().isWithinDistance(finalDest5, 1) || target.tile().isWithinDistance(finalDest6, 1)) {
                    Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy();
                    hit.submit();
                    hit.setDamage(hit.getDamage() / 2);
                }
            }
        }).then(2, () -> { // no sure if 1 after 0 is supported in chains
            vetion.unlock();
            vetion.getCombat().setTarget(lastTarget);
            entity.face(null);
        });
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
