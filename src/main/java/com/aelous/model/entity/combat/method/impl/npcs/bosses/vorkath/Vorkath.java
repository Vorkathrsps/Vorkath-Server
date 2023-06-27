package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.HitMark;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Boundary;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Tuple;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.ZOMBIFIED_SPAWN_8063;
import static com.aelous.model.entity.attributes.AttributeKey.*;

public class Vorkath extends CommonCombatMethod {

    public static final SecureRandom RANDOM = new SecureRandom();

    private static final Animation ATTACK_ANIMATION = new Animation(7952, Priority.HIGH);
    private static final Animation MELEE_ATTACK_ANIMATION = new Animation(7953, Priority.HIGH);
    private static final Animation MELEE_ATTACK_ANIMATION_2 = new Animation(7951, Priority.HIGH);
    public static final Animation FIREBALL_ATTACK_ANIMATION = new Animation(7960, Priority.HIGH);
    private static final Animation FIREBALL_SPIT_ATTACK_ANIMATION = new Animation(7957, Priority.HIGH);
    private static final Graphic RANGED_END_GRAPHIC = new Graphic(1478, GraphicHeight.MIDDLE);
    private static final Graphic MAGIC_END_GRAPHIC = new Graphic(1480, GraphicHeight.MIDDLE);
    private static final Graphic REGULAR_DRAGONFIRE_END_GRAPHIC = new Graphic(157, GraphicHeight.MIDDLE);
    private static final Graphic VENOMOUS_DRAGONFIRE_END_GRAPHIC = new Graphic(1472, GraphicHeight.MIDDLE);
    private static final Graphic PRAYER_DRAGONFIRE_END_GRAPHIC = new Graphic(1473, GraphicHeight.MIDDLE);

    private static final int BREATH_START_HEIGHT = 25;
    private static final int BREATH_END_HEIGHT = 31;
    private static final int BREATH_DELAY = 30;
    private static final int BREATH_DURATION_START = 46;
    private static final int BREATH_DURATION_INCREMENT = 8;
    private static final int BREATH_CURVE = 15;
    private static final int BREATH_OFFSET = 255;
    private static final int TILE_OFFSET = 1;

    boolean poison;

    public enum Resistance {
        PARTIAL, FULL
    }

    public Resistance resistance = null;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(15)) {
            return false;
        }

        if (entity.<Integer>getAttribOr(AttributeKey.VORKATH_CB_COOLDOWN, 0) > 0)
            return false;

        int count = entity.getAttribOr(VORKATH_NORMAL_ATTACK_COUNT, 6);
        int attackType;

        if (count-- < 1) {
            count = 6; // reset back
            int major = entity.<Integer>getAttribOr(VORKATH_LAST_MAJOR_ATTACK, 0) == 0 ? 1 : 0; // switch last attack
            entity.putAttrib(VORKATH_LAST_MAJOR_ATTACK, major);
            attackType = major == 0 ? 6 : 7; // decide the major attack
        } else {
            if (entity.hasAttrib(VORKATH_LINEAR_ATTACKS)) // finish the remaining grouped triple attacks
                attackType = 4;
            else {
                attackType = !CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) ? 2 + RANDOM.nextInt(4) : 1 + RANDOM.nextInt(5);
            }
        }
        entity.putAttrib(VORKATH_NORMAL_ATTACK_COUNT, count);

        switch (attackType) {
            case 1 -> melee();
            case 2 -> mage();
            case 3 -> range();
            case 4 -> tripleOrdered();
            case 6 -> acidSpitball();
            case 7 -> {
                bomb();
            }
        }
        return true;
    }

    private void bomb() {
        entity.animate(FIREBALL_ATTACK_ANIMATION);
        final Tile targetPos = target.tile().copy();
        var tileDist = entity.tile().getChevDistance(targetPos);
        int duration = (85 + -5 + (10 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p1 = new Projectile(tile, targetPos, 1491, 85, duration, 150, 0, 16, entity.getSize(), 10);
        int delay = p1.send(tile, targetPos);
        Task.runOnceTask(delay, t -> {
            if (target.tile().equals(targetPos)) {
                target.hit(entity, Utils.random(121), delay);
                t.stop();
            } else if (target.tile().nextTo(targetPos)) {
                target.hit(entity, Utils.random(60), delay);
                t.stop();
            } else {
                t.stop();
            }
        });
        World.getWorld().tileGraphic(1466, targetPos, GraphicHeight.LOW.ordinal(), p1.getSpeed());
    }

    private void range() {
        entity.animate(ATTACK_ANIMATION);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p = new Projectile(tile, target, 1477, 41, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 16, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, Utils.random(32), delay, CombatType.RANGED).checkAccuracy().submit();
        target.graphic(1478, GraphicHeight.MIDDLE, p.getSpeed());
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void mage() {
        entity.animate(ATTACK_ANIMATION);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p = new Projectile(tile, target, 1479, 51, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 16, entity.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, Utils.random(30), delay, CombatType.MAGIC).checkAccuracy().submit();
        target.graphic(1480, GraphicHeight.MIDDLE, p.getSpeed());
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void melee() {
        entity.animate(MELEE_ATTACK_ANIMATION_2);
        target.hit(entity, Utils.random(32), CombatType.MELEE).checkAccuracy().submit();
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void tripleOrdered() {
        if (!entity.hasAttrib(VORKATH_LINEAR_ATTACKS) || entity.<List<Integer>>getAttrib(VORKATH_LINEAR_ATTACKS).isEmpty()) {
            List<Integer> attackIds = new LinkedList<>(Arrays.asList(0, 1, 2));
            Collections.shuffle(attackIds);
            entity.putAttrib(VORKATH_LINEAR_ATTACKS, attackIds);
        }
        LinkedList<Integer> attackIds = entity.getAttrib(VORKATH_LINEAR_ATTACKS);
        switch (attackIds.pop()) {
            case 0 -> {
                entity.animate(ATTACK_ANIMATION);
                entity.animate(ATTACK_ANIMATION);
                var tileDist = entity.tile().distance(target.tile());
                int duration = (BREATH_DELAY + -5 + (10 * tileDist));
                var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
                Projectile p = new Projectile(tile, target, 1470, BREATH_DELAY, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 16, entity.getSize(), 10);
                final int delay = entity.executeProjectile(p);
                target.graphic(1472, GraphicHeight.MIDDLE, p.getSpeed());
                if (Utils.random(4) <= 3)
                    target.venom(entity);
                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
                fireDamage(hit);
            }
            case 1 -> {
                entity.animate(ATTACK_ANIMATION);
                var tileDist = entity.tile().distance(target.tile());
                int duration = (BREATH_DELAY + -5 + (10 * tileDist));
                var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
                Projectile p = new Projectile(tile, target, 1471, BREATH_DELAY, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 16, entity.getSize(), 10);
                final int delay = entity.executeProjectile(p);
                target.graphic(1473, GraphicHeight.MIDDLE, p.getSpeed());
                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
                fireDamage(hit);
                if (target.isPlayer()) {
                    for (int i = 0; i < target.getAsPlayer().getPrayerActive().length; i++) {
                        Prayers.deactivatePrayer(target, i);
                    }
                    target.getAsPlayer().message("Your prayers have been disabled!");
                }
            }
            case 2 -> {
                var tileDist = entity.tile().distance(target.tile());
                int duration = (BREATH_DELAY + -5 + (10 * tileDist));
                var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
                Projectile p = new Projectile(tile, target, 393, BREATH_DELAY, duration, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 16, entity.getSize(), 10);
                final int delay = entity.executeProjectile(p);
                target.graphic(157, GraphicHeight.MIDDLE, p.getSpeed());
                entity.animate(ATTACK_ANIMATION);
                Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
                fireDamage(hit);
            }
        }
        if (attackIds.isEmpty())
            entity.clearAttrib(VORKATH_LINEAR_ATTACKS);
    }

    private void fireDamage(Hit hit) {
        if (target instanceof Player player) {
            int max = 73;
            var antifire_charges = player.<Integer>getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            var hasShield = CombatConstants.hasAntiFireShield(player);
            var superAntifire = player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false);
            var prayerProtection = Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC);

            //If player is wearing a anti-dragon shield max hit is 20
            if (hasShield) {
                max = 20;
            }

            //If player is using protect from magic max hit is 30
            if (prayerProtection) {
                max = 30;
            }

            //If player is using protect from magic and anti-fire shield max hit 20
            if (hasShield && prayerProtection) {
                max = 20;
            }

            //If player is using anti-fire shield and antifire potion max hit 10
            if (hasShield && antifire_charges > 0) {
                max = 10;
            }

            //If player is using anti-fire shield and super antifire potion max hit 0
            if (hasShield && superAntifire) {
                max = 0;
            }

            //If player is using protect from magic and anti-fire potion max hit remains 20
            if (prayerProtection && antifire_charges > 0) {
                max = 20;
            }

            //If player is using protect from magic and super anti-fire potion max hit is 10
            if (prayerProtection && superAntifire) {
                max = 10;
            }

            //If player is using anti-fire shield, protect from magic and anti-fire potion max hit is 10
            if (hasShield && prayerProtection && antifire_charges > 0) {
                max = 10;
            }

            //If player is using anti-fire shield, protect from magic and super anti-fire potion max hit is 0
            if (hasShield && prayerProtection && superAntifire) {
                max = 0;
            }

            //var hit = World.getWorld().random(max);
            hit.setDamage(World.getWorld().random(max));
            hit.submit();
            if (hit.getDamage() > 30) {
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    private void poisonPools() {
        //mob.forceChat("Poison pools");
        if (!poison) {
            resistance = Resistance.PARTIAL;
            poison = true;
            entity.lockNoDamage();
            Boundary npcBounds = entity.boundaryBounds();
            Boundary poisonBounds = entity.boundaryBounds(6);
            List<GameObject> poisons = new ArrayList<>();
            List<Tile> poisonTiles = new ArrayList<>();
            for (int x = poisonBounds.getMinimumX(); x < poisonBounds.getMaximumX(); x++) {
                for (int y = poisonBounds.getMinimumY(); y < poisonBounds.getMaximumY(); y++) {
                    if (Utils.random(3) == 2) {
                        Tile tile = new Tile(x, y, entity.tile().getLevel());
                        if (!npcBounds.inside(tile)) {
                            GameObject obj = new GameObject(32000, tile, 10, Utils.random(3)).setSpawnedfor(Optional.of(target.getAsPlayer()));
                            poisons.add(obj);
                            poisonTiles.add(tile);
                        }
                    }
                }
            }
            Player[] yo = entity.closePlayers(64);
            for (GameObject object : poisons) {
                for (Player player : yo) {
                    player.getPacketSender().sendObject(object);
                }
            }

            for (Tile poisonTile : poisonTiles) {
                for (Player player : yo) {
                    new Projectile(entity.getCentrePosition(), poisonTile, 1, 1483, 50, 20, 30, 0, 25).sendFor(player);
                }
            }

            Task.repeatingTask(t -> {
                if (finished(entity) || t.tick >= 23) {
                    t.stop();
                } else {
                    for (Player p : yo) {
                        if (poisonTiles.contains(p.tile())) {
                            int hit = Utils.random(8);
                            p.hit(entity, hit, HitMark.POISON);
                            entity.heal(hit);
                        }
                    }
                }
            });

            Chain.bound(null).runFn(1 + 23 + 1, () -> {
                resistance = null;
                poison = false;
                poisons.forEach(object -> {
                    for (Player player : yo) {
                        player.getPacketSender().sendObjectRemoval(object);
                    }
                });
                poisons.clear();
                poisonTiles.clear();
            });
            entity.unlock();
            Chain.bound(null).runFn(23, () -> entity.setEntityInteraction(target));
        }
    }

    private void acidSpitball() {
        entity.animate(FIREBALL_SPIT_ATTACK_ANIMATION);
        poisonPools();
        entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 27);
        entity.runUninterruptable(2, this::startSpitball);
    }

    private void startSpitball() {
        //mob.forceChat("speed spitball");
        final Area area = new Area(2257, 4053, 2286, 4077).transform(0, 0, 0, 0, target.tile().level);
        Optional<Player> first = World.getWorld().getPlayers().search(p -> p.tile().inAreaZ(area));
        var ref = new Object() {
            int loops = 25;
        };

        first.ifPresent(player ->
                Chain.noCtx().cancelWhen(() -> {
                    boolean finished = Vorkath.finished(entity);
                    if (finished) entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
                    return finished;
                }).repeatingTask(1, t -> {
            if (ref.loops-- > 0) {
                Tile landed = player.tile();
                var tileDist = entity.tile().distance(target.tile());
                int duration = (10 + 11 + (5 * tileDist));
                var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
                var projectile = new Projectile(entity, target, 1482, 10, duration, 20, 20, 16, entity.getSize(), 10);
                int delay = projectile.send(tile, target.tile());
                entity.getCombat().delayAttack(0);
                World.getWorld().getPlayers().forEachFiltered(p2 -> p2.tile().equals(landed),
                    p2 -> p2.hit(entity, World.getWorld().random(1, 15),
                        delay));
                return;
            }
            t.stop();
            entity.animate(-1);
        }).then(1, () -> {
            entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
            entity.setEntityInteraction(target);
        }));
    }

    public NPC spawn = null;

    private void zombified() {
        resistance = Resistance.FULL;
        entity.lockNoDamage();
        entity.animate(7960);
        new Projectile(entity, target, 395, BREATH_DELAY, 60, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
        target.freeze(30, entity);
        target.graphic(369, GraphicHeight.LOW, 30);
        Tile pos = Utils.randomElement(entity.getCentrePosition().area(7, t ->
            World.getWorld().meleeClip(t.x, t.y, t.level) == 0
                && t.isWithinDistance(target.getCentrePosition(), 10)
                && !t.isWithinDistance(target.getCentrePosition(), 3)
                && entity.getAsNpc().spawnTile().y - t.y <= 8));
        new Projectile(entity.getCentrePosition(), pos, 0, 1484, 65, 20, 20, 20, 1).sendProjectile();
        entity.repeatingTask(t -> { // ok to run forever, shouldnt get interrupted
            entity.putAttrib(VORKATH_CB_COOLDOWN, 5);
            if (t.tick == 3) {
                spawn = new NPC(ZOMBIFIED_SPAWN_8063, pos).respawns(false);
                spawn.faceEntity(target);
                spawn.getCombat().setTarget(target);
                World.getWorld().registerNpc(spawn);

                //Add the spawn to the instance list
                if (target != null && target instanceof Player) {
                    Player player = (Player) target;
                    player.getInstancedArea().addNpc(spawn);
                }
            } else if (t.tick > 3) {
                if (t.tick > 20) {
                    if (spawn != null && !spawn.dead() && !spawn.finished()) {
                        spawn.hit(spawn, spawn.hp());

                        //Remove the spawn from the instance list
                        if (target != null && target instanceof Player) {
                            Player player = (Player) target;
                            player.getInstancedArea().addNpc(spawn);
                        }
                    }
                }
                if (spawn != null && (spawn.dead() || spawn.finished())) {
                    t.stop();
                    spawn = null;

                    //Remove the spawn from the instance list
                    if (target != null && target instanceof Player) {
                        Player player = (Player) target;
                        player.getInstancedArea().addNpc(spawn);
                    }
                } else {
                    if (spawn != null && spawn.tile().getChevDistance(target.tile()) <= 1) {
                        spawn.hit(spawn, spawn.hp());
                        target.hit(entity, 60, 1);
                        spawn.graphic(157);

                        //Remove the spawn from the instance list
                        if (target != null && target instanceof Player) {
                            Player player = (Player) target;
                            player.getInstancedArea().addNpc(spawn);
                        }
                    }
                }
            }
        }).onStop(() -> {
            resistance = null;
            target.getTimers().cancel(TimerKey.FROZEN);
            target.getTimers().cancel(TimerKey.REFREEZE);
            entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
            entity.unlock();
            entity.getCombat().attack(target);
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 15;
    }

    public static boolean finished(Entity entity) {
        if (entity != null) {
            Tuple<Integer, Player> player = entity.getAsNpc().getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1, null));
            return entity.isNpc() && (entity.dead() || !entity.isRegistered()) || (player.second() != null && (player.second().dead()
                || !player.second().isRegistered() || player.second().tile().getChevDistance(entity.tile()) > 30));
        }
        return false;
    }
}
