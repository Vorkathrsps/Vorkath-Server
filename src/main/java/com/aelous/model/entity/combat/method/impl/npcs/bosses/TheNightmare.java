package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.EntityCombatBuilder;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Boundary;
import com.aelous.model.map.position.Tile;
import com.aelous.model.phase.Phase;
import com.aelous.model.phase.PhaseStage;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aelous.model.entity.attributes.AttributeKey.NIGHTMARE_CURSE;

/**
 * @Author: Origin
 * @Date: 5/21/2023
 */
public class TheNightmare extends CommonCombatMethod {
    private enum Attacks {
        MELEE, RANGE, MAGIC, SPEED_ATTACK, HIDE_ATTACK, SPECIAL_ATTACK
    }

    private Attacks attack = Attacks.MELEE;
    public static final Boundary BOUNDARY = new Boundary(3862, 9940, 3884, 9962);
    @Getter
    public Phase phase = new Phase(PhaseStage.ONE);
    boolean stageOneSpecialAbilitys = phase.getStage() == PhaseStage.ONE;
    boolean stageTwoSpecialAbilitys = phase.getStage() == PhaseStage.TWO;
    boolean stageThreeSpecialAbilitys = phase.getStage() == PhaseStage.THREE;
    @Getter
    @Setter
    boolean huskSpawned, flowerPower, sleepWalking = false;
    @Getter
    int spawnedHuskCount, flowerPowerCount, attackTicks = 0;
    AtomicInteger cursedCount = new AtomicInteger();
    AtomicBoolean cursed = new AtomicBoolean(false);
    public static List<GameObject> objectsList = new ArrayList<>();
    Area safe = new Area(3863, 9951, 3872, 9961, 3);
    Area unsafe1 = new Area(3872, 9951, 3881, 9961, 3);
    Area unsafe2 = new Area(3863, 9941, 3872, 9951, 3);
    Area unsafe3 = new Area(3872, 9941, 3881, 9951, 3);

    @Override
    public void preDefend(Hit hit) {
        hit.setSplatType(SplatType.VERZIK_SHIELD_HITSPLAT);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        if (cursed != null && cursed.get()) {
            cursedCount.getAndIncrement();
        }

        combatBuilder(entity)
            .addAttackConsumer(EntityCombatBuilder.CombatPhase.ALL, t -> meleeClawAttack())
            .addAttackConsumer(EntityCombatBuilder.CombatPhase.ALL, t -> rangeAttack())
            .addAttackConsumer(EntityCombatBuilder.CombatPhase.ALL, t -> magicAttack())
            .setDelayAttack(6)
            .build()
            .distributeAttacks(EntityCombatBuilder.CombatPhase.ALL);
        return true;
    }

    @Override
    public EntityCombatBuilder combatBuilder(Entity entity) {
        return new EntityCombatBuilder(entity, target);
    }

    public void meleeClawAttack() {
        if (!CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            return;
        }
        attack = Attacks.MELEE;
        entity.animate(8594);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 3, CombatType.MELEE);
        hit.submit();
    }

    private void sleepWalker() {
        this.setSleepWalking(true);
        entity.lockDamageOk();
        NPC[] sleepwalkerArray = {
            new NPC(9446, new Tile(3865, 9958, 3)),
            new NPC(9446, new Tile(3865, 9944, 3)),
            new NPC(9446, new Tile(3879, 9944, 3)),
            new NPC(9446, new Tile(3879, 9958, 3))
        };

        List<NPC> sleepwalkerCount = new ArrayList<>(Arrays.asList(sleepwalkerArray));

        World world = World.getWorld();
        world.definitions().get(NpcDefinition.class, 100);

        for (NPC sleepy : sleepwalkerCount) {
            world.registerNpc(sleepy);
            sleepy.respawns(false);
            sleepy.setHitpoints(10);
            sleepy.getMovementQueue().step(entity.getCentrePosition().getX(), entity.getCentrePosition().getY(), MovementQueue.StepType.FORCED_WALK);

            Chain.bound(sleepy)
                .cancelWhen(() -> {
                    if (sleepwalkerCount.isEmpty()) {
                        this.setSleepWalking(false);
                        entity.unlock();
                        return true;
                    }
                    return false;
                }).thenCancellable(1, () -> {
                    sleepy.waitForTile(entity.tile(), () -> {
                        if (sleepy.dead()) {
                            sleepy.remove();
                            sleepwalkerCount.remove(sleepy);
                            return;
                        }
                        entity.healHit(entity, sleepy.hp());
                        target.hit(entity, sleepy.hp());
                        sleepy.die();
                        sleepy.remove();
                        sleepwalkerCount.remove(sleepy);
                    });
                });
        }
    }

    private void curse() {
        Chain.noCtx().cancelWhen(() -> cursedCount.get() >= 5).repeatingTask(1, curseCount -> {
            cursedCount.getAndIncrement();
            cursed.getAndSet(true);

            if (cursedCount.get() == 5) {
                target.clearAttrib(AttributeKey.NIGHTMARE_CURSE);
                if (!target.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                    int hintId = Prayers.getPrayerHeadIcon(target);
                    for (var prayerIndex : Prayers.PROTECTION_PRAYERS) {
                        if (target.getPrayerActive()[prayerIndex]) {
                            target.setHeadHint(hintId);
                        }
                    }
                }
            }
        });

        Task.runOnceTask(1, apply -> {
            if (cursed.get()) {
                target.putAttrib(NIGHTMARE_CURSE, true);
            }

            if (target.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                int hintId = Prayers.getPrayerHeadIcon(target);
                for (var prayerIndex : Prayers.PROTECTION_PRAYERS) {
                    if (target.getPrayerActive()[prayerIndex]) {
                        target.setHeadHint(hintId);
                    }
                }
            }
        });
    }

    private void flowerPower() {
        this.setFlowerPower(true);
        Chain.noCtx().runFn(1, () -> {
            attackTicks++;
            entity.face(null);
            entity.lockDamageOk();
            entity.animate(8607);
        }).then(2, () -> {
            ((NPC) entity).hidden(true);
            entity.teleport(3871, 9950, 3);
        }).then(2, () -> {
            ((NPC) entity).hidden(false);
            entity.animate(8609);
            entity.face(target);
            entity.getCombat().setTarget(target);
            if (entity.isNpc()) {
                entity.getAsNpc().waitForTile(new Tile(3872, 9951, 3), () -> {
                    spawnGameObjects(new Tile(entity.getX(), entity.getY(), entity.getZ()), 10, 11, 37745, 37741);
                });
            }
        });
        Chain.noCtx().cancelWhen(() -> attackTicks == 12).repeatingTask(1, flowerTask -> {
            attackTicks++;
            if (target != null && target.tile() != null) {
                if (target.tile().inArea(unsafe1) || target.tile().inArea(unsafe2) || target.tile().inArea(unsafe3)) {
                    target.hit(entity, 5);
                }
                if (entity.dead()) {
                    for (GameObject obj : objectsList) {
                        obj.remove();
                    }
                    flowerTask.stop();
                }
            }
        }).then(1, () -> {
            objectsList.clear();
            this.setFlowerPower(false);
        });
    }

    private void magicAttack() {
        attack = Attacks.MAGIC;
        entity.animate(8595);
        var tileDist = entity.tile().getChevDistance(target.tile());
        int duration = (80 + -15 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 1764, 80, duration, 90, 30, 0, target.getSize(), 10);
        int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        target.graphic(1765, GraphicHeight.HIGH_3, p.getSpeed());
    }

    private void rangeAttack() {
        attack = Attacks.RANGE;
        entity.animate(8596);
        int tileDist = entity.tile().getChevDistance(target.tile());
        int duration = (90 + 15 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1766, 90, duration, 90, 30, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC);
        hit.submit();
    }

    private void spawnHusk() {
        if (phase.getStage() != PhaseStage.ONE || this.isHuskSpawned()) {
            return;
        }
        this.setHuskSpawned(true);
        List<NPC> huskCount = new ArrayList<>();
        entity.animate(8605);
        final Tile targetPos1 = new Tile(target.getX(), target.getY() + 1, target.getZ());
        final Tile targetPos2 = new Tile(target.getX(), target.getY() - 1, target.getZ());
        var tileDist = entity.tile().distance(target.tile());
        int duration = (80 + 15 + (10 * tileDist));
        Projectile p1 = new Projectile(entity, targetPos1, 1781, 80, duration, 90, 0, 0, target.getSize(), 10);
        p1.send(entity, targetPos1);
        Projectile p2 = new Projectile(entity, targetPos2, 1781, 80, duration, 90, 0, 0, target.getSize(), 10);
        p2.send(entity, targetPos2);

        NPC husk1 = new NPC(9454, new Tile(target.getX(), target.getY() + 1, target.getZ()));
        NPC husk2 = new NPC(9454, new Tile(target.getX(), target.getY() - 1, target.getZ()));

        huskCount.add(husk1);
        huskCount.add(husk2);

        husk1.respawns(false);
        husk2.respawns(false);

        husk1.getAsNpc().getCombatMethod().canMultiAttackInSingleZones();
        husk2.getAsNpc().getCombatMethod().canMultiAttackInSingleZones();

        Task.runOnceTask(4, husk -> {
            World.getWorld().registerNpc(husk1);
            World.getWorld().registerNpc(husk2);
            husk1.animate(8567);
            husk2.animate(8567);
            World.getWorld().definitions().get(NpcDefinition.class, 9454);
            husk1.face(target);
            husk2.face(target);
            husk1.getCombat().setTarget(target);
            husk2.getCombat().setTarget(target);
        });

        target.getMovementQueue().clear();
        target.getMovementQueue().setBlockMovement(true);

        Chain.noCtx().cancelWhen(() -> {
            if (huskCount.isEmpty()) {
                this.setHuskSpawned(false);
                if (target != null) {
                    target.getMovementQueue().setBlockMovement(false);
                }
                return true;
            }
            return false;
        }).repeatingTask(1, count -> {
            if (husk1.dead()) {
                huskCount.remove(husk1);
                World.getWorld().unregisterNpc(husk1);
            }
            if (husk2.dead()) {
                huskCount.remove(husk2);
                World.getWorld().unregisterNpc(husk2);
            }
        });
    }

    public static void spawnGameObjects(Tile baseTile, int tileOne, int tileTwo, int flowerOne, int flowerTwo) {
        GameObject object;
        for (int index = 0; index < tileOne; index++) {
            Tile originalTile = new Tile(baseTile.getX(), baseTile.getY() + index, baseTile.getZ());
            object = new GameObject(flowerOne, originalTile, 10, 0);
            object.spawn();
            objectsList.add(index, object);
        }
        for (int index = 0; index < tileOne; index++) {
            Tile originalTile = new Tile(baseTile.getX() + index, baseTile.getY(), baseTile.getZ());
            object = new GameObject(flowerOne, originalTile, 10, 0);
            object.spawn();
            objectsList.add(index, object);
        }
        for (int index = 0; index < tileTwo; index++) {
            Tile originalTile = new Tile(baseTile.getX() - index, baseTile.getY(), baseTile.getZ());
            object = new GameObject(flowerTwo, originalTile, 10, 0);
            object.spawn();
            objectsList.add(index, object);
        }
        for (int index = 0; index < tileTwo; index++) {
            Tile originalTile = new Tile(baseTile.getX(), baseTile.getY() - index, baseTile.getZ());
            object = new GameObject(flowerTwo, originalTile, 10, 0);
            object.spawn();
            objectsList.add(index, object);
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 12;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }

}
