package com.cryptic.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Boundary;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;
import org.apache.commons.lang3.mutable.MutableObject;

import java.security.SecureRandom;
import java.util.*;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.ZOMBIFIED_SPAWN_8063;
import static com.cryptic.model.entity.attributes.AttributeKey.*;

public class VorkathCombat extends CommonCombatMethod {

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

        if (entity.<Integer>getAttribOr(AttributeKey.VORKATH_CB_COOLDOWN, 0) > 0) return false;

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
                attackType = !withinDistance(1) ? 2 + RANDOM.nextInt(4) : 1 + RANDOM.nextInt(5);
            }
        }
        entity.putAttrib(VORKATH_NORMAL_ATTACK_COUNT, count);

        switch (attackType) {
            case 1 -> melee();
            case 2 -> mage();
            case 3 -> range();
            case 4 -> tripleOrdered();
            case 6 -> acidSpitball();
            case 7 -> bomb();
        }
        return true;
    }

    private void bomb() {
        entity.animate(FIREBALL_ATTACK_ANIMATION);
        final Tile targetPos = target.tile().clone();
        var tileDist = entity.tile().getChevDistance(targetPos);
        int duration = (85 + -5 + (10 * tileDist));
        var tile = entity.tile().translateAndCenterNpcPosition(entity, target);
        Projectile p1 = new Projectile(tile, targetPos, 1491, 85, duration, 150, 0, 16, entity.getSize(), 10);
        final int delay = p1.send(tile, targetPos);
        Chain.noCtx().runFn(delay, () -> {
            if (target.tile().equals(p1.getEnd())) {
                entity.submitAccurateHit(target, 0, Utils.random(1, 121), this);
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
        entity.submitAccurateHit(target, delay, Utils.random(0, 32), this);
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
        entity.submitAccurateHit(target, delay, Utils.random(0, 30), this);
        target.graphic(1480, GraphicHeight.MIDDLE, p.getSpeed());
        Chain.bound(null).runFn(1, () -> entity.setEntityInteraction(target));
    }

    private void melee() {
        entity.animate(MELEE_ATTACK_ANIMATION_2);
        entity.submitAccurateHit(target, 0, Utils.random(0, 32), this);
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
                Hit hit = entity.submitAccurateHit(target, delay, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), this);
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
                Hit hit = entity.submitAccurateHit(target, delay, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), this);
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
                Hit hit = entity.submitAccurateHit(target, delay, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), this);
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
            if (hit.getDamage() > 30) {
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    private void setPoison() {
        if (!poison) {
            resistance = Resistance.PARTIAL;
            poison = true;
            entity.lockNoDamage();
            Boundary npcBounds = entity.boundaryBounds();
            Set<Tile> usedTiles = new HashSet<>(100);
            List<GameObject> poisons = new ArrayList<>(100);
            List<Tile> poisonTiles = new ArrayList<>(100);
            Area INVALID = new Area(2260, 4054, 2284, 4077).transformArea(0, 0, 0, 0, target.getInstancedArea().getzLevel());
            while (usedTiles.size() < 100) {
                var randomTile = INVALID.randomTile();
                if (RegionManager.blocked(randomTile) || npcBounds.inside(randomTile)) continue;
                usedTiles.add(randomTile);
            }

            Player[] players = entity.closePlayers(64);

            for (var tile : usedTiles) {
                GameObject obj = new GameObject(32000, tile, 10, Utils.random(3)).setSpawnedfor(Optional.of(target.getAsPlayer()));
                poisons.add(obj);
                poisonTiles.add(obj.tile());
            }

            MutableObject<Projectile> projectileMutableObject = new MutableObject<>();
            for (Tile poisonTile : poisonTiles) {
                var tileDist = entity.tile().distance(poisonTile);
                int duration = (50 + 25 + (2 * tileDist));
                Projectile p = new Projectile(entity, poisonTile, 1483, 50, duration, 80, 0, 25, entity.getSize(), 2);
                projectileMutableObject.setValue(p);
                p.send(entity, poisonTile);
            }

            Chain
                .noCtx()
                .delay((int) (projectileMutableObject.getValue().getSpeed() / 30D + 1), () -> {
                    for (GameObject object : poisons) {
                        for (Player player : players) {
                            player.getPacketSender().sendObject(object);
                        }
                    }
                }).repeatingTask(1, t -> {
                    if (finished(entity) || t.getRunDuration() >= 23) {
                        t.stop();
                    } else {
                        for (Player p : players) {
                            if (poisonTiles.contains(p.tile())) {
                                int hit = Utils.random(1, 8);
                                p.hit(entity, hit, HitMark.POISON);
                                entity.healHit(entity, hit);
                            }
                        }
                    }
                });

            Chain.bound(null).runFn(1 + 23 + 1, () -> {
                resistance = null;
                poison = false;
                poisons.forEach(object -> {
                    for (Player player : players) {
                        player.getPacketSender().sendObjectRemoval(object);
                    }
                });
                poisons.clear();
                usedTiles.clear();
                poisonTiles.clear();
            });

            entity.unlock();
            Chain.bound(null).runFn(23, () -> entity.setEntityInteraction(target));
        }
    }

    private void acidSpitball() {
        entity.animate(FIREBALL_SPIT_ATTACK_ANIMATION);
        setPoison();
        entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 27);
        Chain.noCtx().runFn(3, this::startSpitBall);
    }

    private void startSpitBall() {
        var ref = new Object() {
            int loops = 25;
        };
        MutableObject<Player> playerMutableObject = new MutableObject<>();
        playerMutableObject.setValue((Player) target);
        Chain.noCtx().cancelWhen(() -> {
            boolean finished = VorkathCombat.finished(entity);
            if (finished) entity.putAttrib(VORKATH_CB_COOLDOWN, 0);
            return finished;
        }).repeatingTask(1, t -> {
            Player target = playerMutableObject.getValue();
            ref.loops--;
            if (ref.loops == 0 || target.getInstancedArea() == null || entity.dead()) {
                entity.animate(-1);
                t.stop();
                return;
            }
            Tile cloneTile = target.tile().clone();
            var tileDist = entity.tile().distance(cloneTile);
            int duration = (10 + 11 + (5 * tileDist));
            var projectile = new Projectile(entity, cloneTile, 1482, 10, duration, 20, 20, 16, entity.getSize(), 10);
            int delay = projectile.send(entity, cloneTile);
            World.getWorld().tileGraphic(131, cloneTile, 0, projectile.getSpeed());
            entity.getCombat().delayAttack(0);
            Chain.noCtx().runFn(1, () -> {
                if (target.tile().equals(cloneTile)) {
                    target.hit(entity, World.getWorld().random(1, 15), delay);
                }
            });
        }).then(1, () -> {
            entity.putAttrib(AttributeKey.VORKATH_CB_COOLDOWN, 0);
            entity.setEntityInteraction(target);
        });
    }

    public NPC spawn = null;

    private void zombified() {
        resistance = Resistance.FULL;
        entity.lockNoDamage();
        entity.animate(7960);
        new Projectile(entity, target, 395, BREATH_DELAY, 60, BREATH_START_HEIGHT, BREATH_END_HEIGHT, 1, true).sendProjectile();
        target.freeze(30, entity, true);
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
            target.getTimers().cancel(TimerKey.FREEZE_IMMUNITY);
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
    public int moveCloseToTargetTileRange(Entity entity) {
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
